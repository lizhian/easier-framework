package easier.framework.test.qo;

import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppQo {


    @Schema(description = "应用账号")
    private String appName;

    @Schema(description = "应用编码")
    private String appCode;

    @Schema(description = "状态")
    private EnableStatus status;

}
