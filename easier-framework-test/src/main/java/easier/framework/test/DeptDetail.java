package easier.framework.test;

import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptDetail {
    @Schema(description = "部门主键")
    private String deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "负责人")
    private String leader;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "电子邮件")
    private String email;

    @Schema(description = "状态")
    private EnableStatus status;
}
