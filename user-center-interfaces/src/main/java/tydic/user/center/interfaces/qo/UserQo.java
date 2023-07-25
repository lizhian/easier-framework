package tydic.user.center.interfaces.qo;

import com.tangzc.mpe.autotable.annotation.Column;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tydic.user.center.interfaces.enums.EnableStatus;

/**
 * 用户请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
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
