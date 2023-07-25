package tydic.user.center.interfaces.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 用户分配角色请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class UserAssignRoleQo {
    @Schema(description = "账号")
    @NotBlank
    private String username;

    @Schema(description = "角色编码")
    private List<String> roleCodes;
}
