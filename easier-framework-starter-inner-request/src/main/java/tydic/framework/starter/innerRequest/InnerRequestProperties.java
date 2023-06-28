package easier.framework.starter.innerRequest;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.inner.request")
public class InnerRequestProperties {


    /**
     * 默认连接超时 默认10秒
     */
    private int connectionTimeout = 10000;
    /**
     * 默认读取超时 默认10秒
     */
    private int readTimeout = 10000;
}
