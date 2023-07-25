package tydic.user.center.interfaces.qo;

import easier.framework.core.plugin.jackson.annotation.ShowDictDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class RoleQo {


    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色类型")
    @ShowDictDetail("role_type")
    private String roleType;

}
