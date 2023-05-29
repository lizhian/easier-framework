package tydic.framework.starter.discovery;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 多缓存源配置
 */
@Data
@ConfigurationProperties(prefix = TydicDiscoveryProperties.prefix)
public class TydicDiscoveryProperties {
    public static final String prefix = "tydic.discovery";
    /**
     * 网卡名称
     */
    private String networkCard;
    private boolean secure;
    private Map<String, String> metadata = new HashMap<>();
    private int weight = -1;
    private String group = "default";
}
