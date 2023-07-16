package easier.framework.starter.discovery;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.starter.cache.EnableEasierCache;
import easier.framework.starter.discovery.core.RedissonDiscoveryClient;
import easier.framework.starter.discovery.core.RedissonServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@EnableConfigurationProperties(EasierDiscoveryProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableEasierCache
@EnableSpringUtil
@AutoConfigureBefore(CompositeDiscoveryClientAutoConfiguration.class)
@Import({RedissonServiceRegistry.class, RedissonDiscoveryClient.class})
public class EasierDiscoveryAutoConfiguration {
}
