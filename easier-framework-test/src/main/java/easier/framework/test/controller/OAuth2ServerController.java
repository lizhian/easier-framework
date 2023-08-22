package easier.framework.test.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import cn.dev33.satoken.oauth2.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.model.CodeModel;
import cn.dev33.satoken.oauth2.model.RequestAuthModel;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import cn.hutool.core.util.URLUtil;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.auth.AuthContext;
import easier.framework.core.plugin.exception.ExceptionHandlerRegister;
import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.ValidUtil;
import easier.framework.core.util.WebUtil;
import easier.framework.test.enums.Oauth2ResponseType;
import easier.framework.test.qo.OAuthAuthorizeQo;
import easier.framework.test.qo.OAuthTokenQo;
import easier.framework.test.vo.Oauth2TokenVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录控制器
 *
 * @author lizhian
 * @date 2023年07月26日
 */
@Tag(name = "OAuth2认证服务端")
@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuth2ServerController {

    static {
        ExceptionHandlerRegister.register(SaOAuth2Exception.class, error -> {
            log.error(error.getMessage(), error);
            return R.failed(error.getMessage());
        });
    }


    @Operation(summary = "oauth2认证授权")
    @GetMapping("/oauth2/authorize")
    public void authorize(@Validated OAuthAuthorizeQo qo) {
        String client_id = qo.getClient_id();
        String redirect_uri = qo.getRedirect_uri();
        boolean question_mark = qo.isQuestion_mark();
        log.info("进入oauth2认证授权, client_id= {} question_mark= {} redirect_uri= {} ", client_id, question_mark, redirect_uri);
        checkClient(qo);
        // 如果用户尚未登录, 重定向登录页面进行登录
        if (!AuthContext.isLogin()) {
            redirectToLoginHtml();
            return;
        }
        //当前用户的账号
        String account = AuthContext.getAccount();
        // 校验重定向域名是否合法
        SaOAuth2Util.checkRightUrl(client_id, redirect_uri);
        //最终重定向页面
        String finalRedirectUri = buildFinalRedirectUri(qo, account);
        if (question_mark) {
            finalRedirectUri = finalRedirectUri.replace("#token=", "?token=");
        }
        log.info("用户已登录: {}, 重定向至: {}", account, finalRedirectUri);
        SaHolder.getResponse().redirect(finalRedirectUri);
    }

    /**
     * 构建最终重定向uri
     *
     * @param qo      请求对象
     * @param account 账户
     * @return {@link String}
     */
    private String buildFinalRedirectUri(OAuthAuthorizeQo qo, String account) {
        RequestAuthModel authModel = qo.toRequestAuthModel(account);
        String redirect_uri = qo.getRedirect_uri();
        //授权码模式,构建重定向地址
        if (qo.getResponse_type().equals(Oauth2ResponseType.code)) {
            CodeModel codeModel = SaOAuth2Util.generateCode(authModel);
            return SaOAuth2Util.buildRedirectUri(redirect_uri, codeModel.code, authModel.state);
        }
        //隐藏模式,构建重定向地址
        AccessTokenModel tokenModel = SaOAuth2Util.generateAccessToken(authModel, false);
        return SaOAuth2Util.buildImplicitRedirectUri(redirect_uri, tokenModel.accessToken, authModel.state);
    }


    /**
     * 检查客户端
     *
     * @param qo 请求对象
     */
    private void checkClient(OAuthAuthorizeQo qo) {
        SaOAuth2Config auth2Config = SaOAuth2Manager.getConfig();
        SaClientModel client = SaOAuth2Util.checkClientModel(qo.getClient_id());
        //授权码模式,返回code
        if (qo.getResponse_type().equals(Oauth2ResponseType.code)) {
            if (!auth2Config.isCode) {
                throw BizException.of("暂未开放授权码模式");
            }
            if (!client.isCode && !client.isAutoMode) {
                throw BizException.of("应用不支持授权码模式");
            }
            return;
        }
        //隐藏式,返回token
        if (!auth2Config.isImplicit) {
            throw BizException.of("暂未开放隐藏式授权模式");
        }
        if (!client.isImplicit && !client.isAutoMode) {
            throw BizException.of("应用不支持隐藏式授权模式");
        }
    }

    /**
     * 重定向到登录html
     */
    private void redirectToLoginHtml() {
        String queryString = WebUtil.getRequestOpt()
                .map(HttpServletRequest::getQueryString)
                .orElse(StrUtil.EMPTY);
        String url = SaHolder.getRequest().getUrl() + "?" + queryString;
        log.info("重定向至登录页面, src_url= {}", url);
        SaHolder.getResponse().redirect("./../login.html?redirect_uri=" + URLUtil.encodeAll(url));
    }


    @Operation(summary = "通过code获取access_token")
    @GetMapping("/oauth2/token")
    public R<Oauth2TokenVo> token(OAuthTokenQo qo) {
        ValidUtil.valid(qo, OAuthTokenQo.CodeGroup.class);
        String code = qo.getCode();
        String client_id = qo.getClient_id();
        String client_secret = qo.getClient_secret();
        SaOAuth2Util.checkGainTokenParam(code, client_id, client_secret, null);
        AccessTokenModel tokenModel = SaOAuth2Util.generateAccessToken(code);
        Oauth2TokenVo tokenVo = Oauth2TokenVo.builder()
                .access_token(tokenModel.accessToken)
                .refresh_token(tokenModel.refreshToken)
                .expires_in(tokenModel.getExpiresIn())
                .refresh_expires_in(tokenModel.getRefreshExpiresIn())
                .client_id(client_id)
                .account(tokenModel.loginId.toString())
                .build();
        return R.success(tokenVo);
    }

    @Operation(summary = "通过refresh_token获取access_token")
    @GetMapping("/oauth2/refresh")
    public R<Oauth2TokenVo> refresh(OAuthTokenQo qo) {
        ValidUtil.valid(qo, OAuthTokenQo.RefreshGroup.class);
        String client_id = qo.getClient_id();
        String client_secret = qo.getClient_secret();
        String refresh_token = qo.getRefresh_token();
        SaOAuth2Util.checkRefreshTokenParam(client_id, client_secret, refresh_token);
        AccessTokenModel tokenModel = SaOAuth2Util.refreshAccessToken(refresh_token);
        Oauth2TokenVo tokenVo = Oauth2TokenVo.builder()
                .access_token(tokenModel.accessToken)
                .refresh_token(tokenModel.refreshToken)
                .expires_in(tokenModel.getExpiresIn())
                .refresh_expires_in(tokenModel.getRefreshExpiresIn())
                .client_id(client_id)
                .account(tokenModel.loginId.toString())
                .build();
        return R.success(tokenVo);
    }

    @Operation(summary = "回收access_token")
    @GetMapping("/oauth2/revoke")
    public R<String> revoke(OAuthTokenQo qo) {
        ValidUtil.valid(qo, OAuthTokenQo.RevokeGroup.class);
        String client_id = qo.getClient_id();
        String client_secret = qo.getClient_secret();
        String access_token = qo.getAccess_token();
        // 如果 Access-Token 不存在，直接返回
        if (SaOAuth2Util.getAccessToken(access_token) == null) {
            return R.success("access_token不存在：" + access_token);
        }
        // 校验参数
        SaOAuth2Util.checkAccessTokenParam(client_id, client_secret, access_token);
        // 回收 Access-Token
        SaOAuth2Util.revokeAccessToken(access_token);
        return R.success();
    }
}
