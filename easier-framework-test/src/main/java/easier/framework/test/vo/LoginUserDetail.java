package easier.framework.test.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class LoginUserDetail {

    @Schema(description = "token值")
    private String tokenValue;

    @Schema(description = "token名称,请求头名称")
    private String tokenName;

    @Schema(description = "token前缀")
    private String tokenPrefix;

    @Schema(description = "用户主键")
    private String userId;

    @Schema(description = "用户账号")
    private String username;

    @Schema(description = "用户名称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "所属部门主键")
    private String deptId;

    @Schema(description = "所属部门名称")
    private String deptName;

    @Schema(description = "使用的角色编码")
    private List<String> roleCodes;

    @Schema(description = "使用的权限编码")
    private List<String> perms;
}
