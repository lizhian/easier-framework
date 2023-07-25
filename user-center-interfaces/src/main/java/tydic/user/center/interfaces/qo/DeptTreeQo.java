package tydic.user.center.interfaces.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tydic.user.center.interfaces.enums.EnableStatus;

/**
 * 部门树请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class DeptTreeQo {

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "部门状态")
    private EnableStatus status;

    @Schema(description = "排除部门")
    private String excludeDeptId;
}
