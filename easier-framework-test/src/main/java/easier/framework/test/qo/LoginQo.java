package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class LoginQo {

    @Schema(description = "用户账号")
    @NotBlank
    private String username;

    @Schema(description = "密码")
    @NotBlank
    private String password;

    @Schema(description = "验证码")
    @NotBlank
    private String code;

    @Schema(description = "验证码主键")
    @NotBlank
    private String captchaId;

}
