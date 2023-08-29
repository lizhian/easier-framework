package easier.framework.starter.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 多缓存源配置
 */
@Data
@ConfigurationProperties(prefix = EasierCacheProperties.prefix)
public class EasierCacheProperties {
    public static final String prefix = "spring.easier.cache";
    private String enableRedis;
    private Map<String, RedissonProperties> redis = new HashMap<>();


    public enum Type {
        single,
        sentinel,
        cluster;
    }

    @Data
    public static class RedissonProperties {
        private String alias;
        private Type type = Type.single;
        private String nodes = "127.0.0.1:6379";
        private int database = 0;
        private String password;
        private boolean ssl = false;
        private int connectTimeoutMillis = 10000;
        private Single single;
        private Sentinel sentinel;
        private Cluster cluster;
    }

    @Data
    public static class Single {
    }

    @Data
    public static class Sentinel {
        private String masterName;

    }

    @Data
    public static class Cluster {
    }

}
