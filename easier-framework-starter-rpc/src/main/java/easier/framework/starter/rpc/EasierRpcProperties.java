package easier.framework.starter.rpc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 更简单mybatis属性
 *
 * @author lizhian
 * @date 2023年07月05日
 */
@Getter
@Setter
@ConfigurationProperties(prefix = EasierRpcProperties.PREFIX)
public class EasierRpcProperties {

    public static final String PREFIX = "spring.easier.rpc";

    /**
     * 默认连接超时 默认10秒
     */
    private int connectionTimeout = 10000;
    /**
     * 默认读取超时 默认10秒
     */
    private int readTimeout = 10000;
}
