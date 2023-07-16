package easier.framework.starter.discovery.core;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

/**
 * redisson服务实例
 *
 * @author lizhian
 * @date 2023年07月16日
 */
@Data
@FieldNameConstants
public class RedissonServiceInstance implements ServiceInstance {
    private String instanceId;
    private String serviceId;
    private String host;
    private int port;
    private boolean secure;
    private int weight;
    private String group;
    private Map<String, String> metadata;

    @Override
    public URI getUri() {
        return DefaultServiceInstance.getUri(this);
    }

}
