package easier.framework.test.qo;

import com.tangzc.mpe.autotable.annotation.Column;
import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserQo {

    @Column(comment = "部门主键")
    private String deptId;

    @Schema(description = "用户账号")
    private String username;

    @Schema(description = "用户名称")
    private String nickname;

    @Schema(description = "状态")
    private EnableStatus status;
}
