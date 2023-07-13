package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordQo {
    @Schema(description = "账号")
    @NotBlank
    private String username;

    @Schema(description = "新密码")
    @NotBlank
    private String newPassword;
}
