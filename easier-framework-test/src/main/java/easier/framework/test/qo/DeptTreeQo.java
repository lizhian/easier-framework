package easier.framework.test.qo;

import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DeptTreeQo {

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "部门状态")
    private EnableStatus status;

    @Schema(description = "排除部门")
    private String excludeDeptId;
}
