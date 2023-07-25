package tydic.user.center.interfaces.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 重置密码请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class ResetPasswordQo {
    @Schema(description = "账号")
    @NotBlank
    private String username;

    @Schema(description = "新密码")
    @NotBlank
    private String newPassword;
}
