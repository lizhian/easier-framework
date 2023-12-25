package easier.framework.starter.discovery;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.plugin.cache.RedisSources;
import easier.framework.starter.cache.EnableEasierCache;
import easier.framework.starter.cache.condition.ConditionalOnRedisSource;
import easier.framework.starter.cache.redis.RedissonClients;
import easier.framework.starter.discovery.core.RedissonDiscoveryClient;
import easier.framework.starter.discovery.core.RedissonServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableConfigurationProperties(EasierDiscoveryProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableEasierCache
@EnableSpringUtil
@AutoConfigureBefore(CompositeDiscoveryClientAutoConfiguration.class)
public class EasierDiscoveryAutoConfiguration {

    @Bean
    @ConditionalOnBean(RedissonClients.class)
    @ConditionalOnRedisSource(RedisSources.discovery)
    public RedissonDiscoveryClient redissonDiscoveryClient(RedissonClients redissonClients) {
        return new RedissonDiscoveryClient(redissonClients);
    }

    @Bean
    @ConditionalOnBean(RedissonClients.class)
    @ConditionalOnRedisSource(RedisSources.discovery)
    @ConditionalOnProperty(prefix = "spring.easier.discovery", name = "registry", havingValue = "true", matchIfMissing = true)
    @ConditionalOnWebApplication
    public RedissonServiceRegistry redissonServiceRegistry(EasierDiscoveryProperties properties, RedissonClients redissonClients) {
        return new RedissonServiceRegistry(properties, redissonClients);
    }
}
