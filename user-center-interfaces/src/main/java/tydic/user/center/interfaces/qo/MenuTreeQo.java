package tydic.user.center.interfaces.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tydic.user.center.interfaces.enums.EnableStatus;
import tydic.user.center.interfaces.enums.MenuType;

import javax.validation.constraints.NotBlank;

/**
 * 菜单树请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class MenuTreeQo {

    @Schema(description = "应用编码")
    @NotBlank
    private String appCode;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单类型")
    private MenuType menuType;

    @Schema(description = "状态")
    private EnableStatus status;
}
