package easier.framework.test.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import easier.framework.core.domain.R;
import easier.framework.core.util.IdUtil;
import easier.framework.test.qo.LoginQo;
import easier.framework.test.service.LoginService;
import easier.framework.test.vo.CaptchaDetail;
import easier.framework.test.vo.LoginUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 登录控制器
 *
 * @author lizhian
 * @date 2023年07月26日
 */
@Tag(name = "登录")
@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final MathGenerator generator = new MathGenerator(1);


    @Operation(summary = "密码登录接口")
    @PostMapping("/login/password")
    public R<LoginUserDetail> login(@RequestBody @Validated LoginQo qo) {
        LoginUserDetail detail = this.loginService.login(qo);
        return R.success(detail);
    }


    @Operation(summary = "验证码")
    @GetMapping("/login/captcha")
    public R<CaptchaDetail> captcha() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(80, 32, 1, 32);
        captcha.setGenerator(this.generator);
        String code = captcha.getCode();
        String imageBase64Data = captcha.getImageBase64Data();
        String captchaId = IdUtil.nextIdStr();
        CaptchaDetail build = CaptchaDetail.builder()
                .captchaId(captchaId)
                .imageBase64Data(imageBase64Data)
                .build();
        return R.success(build);
    }


}
