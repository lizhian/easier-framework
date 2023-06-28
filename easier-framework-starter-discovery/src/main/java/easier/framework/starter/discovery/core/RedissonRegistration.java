package easier.framework.starter.discovery.core;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.net.URI;
import java.util.Map;

@Data
@FieldNameConstants
public class RedissonRegistration implements Registration {
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
