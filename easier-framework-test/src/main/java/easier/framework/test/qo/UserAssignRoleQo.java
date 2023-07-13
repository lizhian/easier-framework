package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class UserAssignRoleQo {
    @Schema(description = "账号")
    @NotBlank
    private String username;

    @Schema(description = "角色编码")
    private List<String> roleCodes;
}
