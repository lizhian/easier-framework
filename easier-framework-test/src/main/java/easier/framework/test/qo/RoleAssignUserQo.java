package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class RoleAssignUserQo {
    @Schema(description = "角色编码")
    @NotBlank
    private String roleCode;

    @Schema(description = "用户账号列表")
    @NotEmpty
    private List<String> usernames;
}
