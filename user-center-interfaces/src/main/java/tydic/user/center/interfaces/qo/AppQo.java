package tydic.user.center.interfaces.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tydic.user.center.interfaces.enums.EnableStatus;

/**
 * app请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class AppQo {


    @Schema(description = "应用账号")
    private String appName;

    @Schema(description = "应用编码")
    private String appCode;

    @Schema(description = "状态")
    private EnableStatus status;

}
