package easier.framework.starter.discovery;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lizhian
 * @date 2023年07月16日
 */
@Data
@ConfigurationProperties(prefix = EasierDiscoveryProperties.prefix)
public class EasierDiscoveryProperties {
    public static final String REDIS_KEY_PREFIX = "Easier:Discovery:";
    public static final String prefix = "spring.easier.discovery";
    public boolean registry = true;
    /**
     * host优先获取,可以填写网卡名称或者网段(ip前缀)
     */
    private String hostPriorities;
    private boolean secure;
    private int weight = -1;
    private String group = "default";
    private Map<String, String> metadata = new HashMap<>();

}
