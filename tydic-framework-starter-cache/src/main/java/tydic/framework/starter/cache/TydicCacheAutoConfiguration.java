package tydic.framework.starter.cache;

import cn.hutool.extra.spring.EnableSpringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.cache.builder.CacheBuilderInvoker;
import tydic.framework.starter.cache.redis.RedissonClients;
import tydic.framework.starter.cache.redis.RedissonConfigCustomizer;
import tydic.framework.starter.cache.redis.RedissonJacksonCodec;
import tydic.framework.starter.cache.redis.RedissonTemplate;
import tydic.framework.starter.jackson.EnableTydicJackson;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(TydicCacheProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableTydicJackson
@EnableSpringUtil
@Import(CacheBuilderInvoker.class)
public class TydicCacheAutoConfiguration {

    @Bean
    public RedissonConfigCustomizer defaultRedissonConfigCustomizer() {
        return configuration -> {
            configuration.setExecutor(SpringUtil.getExecutor().getThreadPoolExecutor());
            configuration.setCodec(new RedissonJacksonCodec());
        };
    }


    @Bean
    public RedissonClients dynamicRedissonClient(TydicCacheProperties tydicCacheProperties) {
        RedissonClients redissonClients = new RedissonClients();
        redissonClients.init(tydicCacheProperties);
        return redissonClients;
    }

    @Bean
    public RedissonTemplate redissonTemplate(RedissonClients redissonClients) {
        return redissonClients.getTemplate();
    }
}
