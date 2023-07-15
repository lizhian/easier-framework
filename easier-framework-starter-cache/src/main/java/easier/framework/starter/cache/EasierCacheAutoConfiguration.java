package easier.framework.starter.cache;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.plugin.cache.container.CacheContainerHelper;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.cache.builder.DefaultCacheContainerHelper;
import easier.framework.starter.cache.redis.RedissonClients;
import easier.framework.starter.cache.redis.RedissonConfigCustomizer;
import easier.framework.starter.cache.redis.RedissonJacksonCodec;
import easier.framework.starter.cache.redis.RedissonTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(easier.framework.starter.cache.EasierCacheProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
@Import(DefaultCacheContainerHelper.class)
public class EasierCacheAutoConfiguration {

    @Bean
    public RedissonConfigCustomizer defaultRedissonConfigCustomizer() {
        return configuration -> {
            configuration.setExecutor(SpringUtil.getExecutor().getThreadPoolExecutor());
            configuration.setCodec(new RedissonJacksonCodec());
        };
    }


    @Bean
    public RedissonClients dynamicRedissonClient(easier.framework.starter.cache.EasierCacheProperties easierCacheProperties) {
        RedissonClients redissonClients = new RedissonClients();
        redissonClients.init(easierCacheProperties);
        return redissonClients;
    }

    @Bean
    public RedissonTemplate redissonTemplate(RedissonClients redissonClients) {
        return redissonClients.getTemplate();
    }

    @Bean
    public RedissonClient redissonClient(RedissonClients redissonClients) {
        return redissonClients.getClient();
    }

    @Bean
    public CacheContainerHelper cacheContainerHelper(RedissonClients redissonClients) {
        return new DefaultCacheContainerHelper(redissonClients);
    }
}
