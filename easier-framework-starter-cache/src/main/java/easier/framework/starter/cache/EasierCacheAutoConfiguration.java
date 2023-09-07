package easier.framework.starter.cache;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.plugin.cache.RedisSources;
import easier.framework.core.plugin.cache.container.CacheContainerHelper;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.cache.builder.DefaultCacheContainerHelper;
import easier.framework.starter.cache.condition.ConditionalOnRedisSource;
import easier.framework.starter.cache.event.BroadcastEventListener;
import easier.framework.starter.cache.redis.RedissonClients;
import easier.framework.starter.cache.redis.RedissonConfigCustomizer;
import easier.framework.starter.cache.redis.RedissonJacksonCodec;
import easier.framework.starter.cache.redis.RedissonTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(EasierCacheProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
public class EasierCacheAutoConfiguration {

    @Bean
    public RedissonConfigCustomizer defaultRedissonConfigCustomizer() {
        return configuration -> {
            ThreadPoolTaskExecutor executor = SpringUtil.getExecutor();
            if (executor != null) {
                configuration.setExecutor(executor.getThreadPoolExecutor());
            }
            configuration.setCodec(new RedissonJacksonCodec());
        };
    }


    @Bean
    public RedissonClients redissonClients(EasierCacheProperties easierCacheProperties) {
        RedissonClients redissonClients = new RedissonClients();
        redissonClients.init(easierCacheProperties);
        return redissonClients;
    }

    @Bean
    @ConditionalOnProperty("spring.easier.cache.enable-redis")
    public RedissonTemplate redissonTemplate(RedissonClients redissonClients) {
        return redissonClients.getTemplate();
    }

    @Bean
    @ConditionalOnProperty("spring.easier.cache.enable-redis")
    public RedissonClient redissonClient(RedissonClients redissonClients) {
        return redissonClients.getClient();
    }

    @Bean
    public CacheContainerHelper defaultCacheContainerHelper(RedissonClients redissonClients) {
        return new DefaultCacheContainerHelper(redissonClients);
    }


    @Bean
    @ConditionalOnRedisSource(RedisSources.event)
    public BroadcastEventListener broadcastEventListener(RedissonClients redissonClients) {
        return new BroadcastEventListener(redissonClients);
    }
}
