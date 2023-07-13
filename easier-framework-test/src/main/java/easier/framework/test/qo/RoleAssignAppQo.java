package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;

public class RoleAssignAppQo {
    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "应用编码")
    private String appCodes;
}
