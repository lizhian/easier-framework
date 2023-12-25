package easier.framework.starter.cache;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.plugin.cache.RedisSources;
import easier.framework.core.plugin.cache.container.CacheContainerHelper;
import easier.framework.core.plugin.message.MessageBean;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.cache.condition.ConditionalOnRedisSource;
import easier.framework.starter.cache.event.BroadcastEventListener;
import easier.framework.starter.cache.helper.DefaultCacheContainerHelper;
import easier.framework.starter.cache.message.MessageConsumerMethodContainer;
import easier.framework.starter.cache.message.RedisMessageContainerFactory;
import easier.framework.starter.cache.redis.RedissonClients;
import easier.framework.starter.cache.redis.RedissonConfigCustomizer;
import easier.framework.starter.cache.redis.RedissonJacksonCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

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

    @Bean
    public RedisMessageContainerFactory redisMessageContainerFactory(RedissonClients redissonClients) {
        return new RedisMessageContainerFactory(redissonClients);
    }

    /**
     * 当应用准备就绪时触发的事件监听方法
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startMessageConsumerMethod() {
        /*
          获取所有MessageBean类型的bean实例的映射关系
         */
        Map<String, MessageBean> beans = SpringUtil.getBeansOfType(MessageBean.class);
        /*
          遍历每个bean实例及其方法
         */
        for (Map.Entry<String, MessageBean> entry : beans.entrySet()) {
            MessageBean bean = entry.getValue();
            for (Method beanMethod : ReflectUtil.getMethods(bean.getClass())) {
                /*
                  获取bean实现的接口中的方法
                 */
                Method interfaceMethod = Arrays.stream(beanMethod.getDeclaringClass().getInterfaces())
                        .filter(it -> ArrayUtil.contains(it.getInterfaces(), MessageBean.class))
                        .findAny()
                        .map(it -> ReflectUtil.getMethod(it, beanMethod.getName(), beanMethod.getParameterTypes()))
                        .orElse(null);
                /*
                  如果接口方法为空或者为默认方法，则结束当前循环
                 */
                if (interfaceMethod == null || interfaceMethod.isDefault()) {
                    return;
                }
                /*
                  创建并启动MessageConsumerMethodContainer实例
                 */
                new MessageConsumerMethodContainer(bean, beanMethod, interfaceMethod).start();
            }
        }
    }

}
