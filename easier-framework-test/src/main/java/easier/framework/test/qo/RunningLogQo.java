package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 运行日志请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class RunningLogQo {

    @Schema(description = "追踪码")
    private String traceId;

    @Schema(description = "记录服务IP")
    private String serverName;

    @Schema(description = "应用名")
    private String appName;

    @Schema(description = "应用环境")
    private String env;

    @Schema(description = "日志等级")
    private String logLevel;
}
