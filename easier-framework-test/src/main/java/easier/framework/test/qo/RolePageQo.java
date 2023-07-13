package easier.framework.test.qo;

import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RolePageQo {


    @Schema(description = "角色名称")
    private String appName;

    @Schema(description = "角色编码")
    private String appCode;

    @Schema(description = "状态")
    private EnableStatus status;

}
