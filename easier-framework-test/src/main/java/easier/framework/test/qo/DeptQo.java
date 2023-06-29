package easier.framework.test.qo;

import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DeptQo {

    @Schema(description = "父部门ID")
    private String parentId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "负责人")
    private String leader;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "部门状态")
    private EnableStatus status;
}
