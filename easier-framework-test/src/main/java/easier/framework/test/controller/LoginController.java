package easier.framework.test.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Consts;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import easier.framework.core.Easier;
import easier.framework.core.domain.IdQo;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.cache.container.CacheContainer;
import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.plugin.jackson.ObjectMapperHolder;
import easier.framework.core.util.SpringUtil;
import easier.framework.test.UserDetail;
import easier.framework.test.qo.LoginRedirectQo;
import easier.framework.test.qo.PasswordLoginQo;
import easier.framework.test.service.LoginService;
import easier.framework.test.vo.CaptchaDetail;
import easier.framework.test.vo.LoginUserDetail;
import easier.framework.test.vo.Oauth2TokenVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * 登录控制器
 *
 * @author lizhian
 * @date 2023年07月26日
 */
@Slf4j
@Tag(name = "登录")
@RestController
@RequiredArgsConstructor
public class LoginController {

    static {
        CacheContainer<UserDetail> cache = Easier.Cache
                .newCache(UserDetail.class)
                .keyPrefix("hahah")
                .source("spring")
                .timeToLiveForever()
                .enableLocalCache(1000, Duration.ofSeconds(5))
                .build();
        // UserDetail s = cache.get("1");


        // 未登录异常
        /*ExceptionHandlerRegister.register(NotLoginException.class, HttpStatus.UNAUTHORIZED, exception -> {
            log.error(exception.getMessage());
            R<UnLoginVo> failed = R.failed(exception.getMessage());
            failed.setCode(RCode.not_login.getValue());
            UnLoginVo vo = UnLoginVo.builder()
                    .redirect_uri("http://127.0.0.1:8080/login/redirect?question_mark=true&frontend_uri=")
                    .append_frontend_uri(true)
                    .encode_frontend_uri(true)
                    .build();
            failed.setData(vo);
            return failed;
        });*/
    }

    private final LoginService loginService;

    public static void main(String[] args) {
        System.out.println(URLUtil.encodeAll("http://192.168.2.23:8080/login/code?use_question_mark=true&redirect_uri="));
        String encoded = URLUtil.encodeAll("http://192.168.2.23:8080/index.html");
        System.out.println(encoded);
        System.out.println(URLUtil.encodeAll("http://192.168.2.23:8080/login/code?use_question_mark=true&redirect_uri=") + encoded);

    }

    @Operation(summary = "密码登录接口")
    @PostMapping("/login/password")
    public R<LoginUserDetail> login(@RequestBody @Validated PasswordLoginQo qo) {
        LoginUserDetail detail = this.loginService.login(qo);
        return R.success(detail);
    }

    @Operation(summary = "验证码")
    @GetMapping("/login/captcha")
    public R<CaptchaDetail> captcha(IdQo idQo) {
        CaptchaDetail data = this.loginService.captcha(idQo.getId());
        return R.success(data);
    }

    @Operation(summary = "登录重定向记录")
    @GetMapping("/login/redirect")
    public void redirect(@Validated LoginRedirectQo qo) {
        String redirectId = Easier.Id.nextIdStr();
        // UserCenterCaches.LOGIN_REDIRECT.update(redirectId, qo);
        String authorizeAddress = "http://192.168.2.23:8080/oauth2/authorize";
        String clientId = "1000";
        String redirectUri = "http://127.0.0.1:8080/login/code/" + redirectId;
        SaHolder.getResponse().redirect(StrUtil.format(
                "{}?response_type=code&client_id={}&redirect_uri={}"
                , authorizeAddress, clientId, redirectUri
        ));
    }

    @Operation(summary = "授权码登录接口")
    @GetMapping("/login/code/{redirectId}")
    public R<Oauth2TokenVo> login(String code, @PathVariable String redirectId) {
        if (StrUtil.isBlank(code)) {
            throw BizException.of("code授权码不能为空");
        }
        if (StrUtil.isBlank(redirectId)) {
            throw BizException.of("无效的授权请求");
        }
        // LoginRedirectQo loginRedirectQo = UserCenterCaches.LOGIN_REDIRECT.get(redirectId);
        /*if (loginRedirectQo == null) {

            throw BizException.of("无效的授权请求或者授权请求已过期");
        }*/
        @Cleanup HttpResponse execute = HttpUtil
                .createGet("http://192.168.2.23:8080/oauth2/token")
                .header(Easier.TraceId.x_trace_id, Easier.TraceId.getOrReset())
                .form(SaOAuth2Consts.Param.code, code)
                .form(SaOAuth2Consts.Param.client_id, "1000")
                .form(SaOAuth2Consts.Param.client_secret, "1000")
                .execute();
        String body = execute.body();
        R<Oauth2TokenVo> responseBody;
        try {
            responseBody = ObjectMapperHolder.get().readValue(body, new TypeReference<R<Oauth2TokenVo>>() {
            });
        } catch (JsonProcessingException e) {
            return R.failed("单点登录服务异常:" + body + ",请联系管理员");
        }
        if (responseBody.getCode() != 200) {
            return R.failed("单点登录服务异常:" + responseBody.getMessage() + ",请联系管理员");
        }
        Oauth2TokenVo tokenVo = responseBody.getData();
        String access_token = tokenVo.getAccess_token();
        String account = tokenVo.getAccount();
        Easier.Auth.login(account, model -> model
                .setToken(access_token)
                .setDevice(SpringUtil.getApplicationName())
        );
        log.info("用户登录,account={},token={}", account, access_token);
        /*UserCenterCaches.LOGIN_REDIRECT.clean(redirectId);
        String frontend_uri = loginRedirectQo.getFrontend_uri();
        if (StrUtil.isBlank(frontend_uri)) {
            return R.success(tokenVo);
        }
        log.info("授权码登录成功,重定向前端链接: {}", frontend_uri);
        String finalRedirectUrl = loginRedirectQo.isQuestion_mark()
                ? SaFoxUtil.joinParam(frontend_uri, SaOAuth2Consts.Param.token, access_token)
                : SaFoxUtil.joinSharpParam(frontend_uri, SaOAuth2Consts.Param.token, access_token);
        SaHolder.getResponse().redirect(finalRedirectUrl);*/
        return null;
    }


}
