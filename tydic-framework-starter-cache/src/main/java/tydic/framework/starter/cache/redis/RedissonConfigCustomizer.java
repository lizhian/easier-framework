package tydic.framework.starter.cache.redis;

import org.redisson.config.Config;

public interface RedissonConfigCustomizer {
    void customize(Config config);
}
