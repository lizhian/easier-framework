package tydic.framework.starter.mq;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import tydic.framework.core.plugin.mq.MQ;
import tydic.framework.core.plugin.mq.annotation.MQListener;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.cache.EnableTydicCache;
import tydic.framework.starter.cache.redis.RedissonClients;
import tydic.framework.starter.mq.builder.MQBuilderInvoker;
import tydic.framework.starter.mq.builder.MQMethodDetail;
import tydic.framework.starter.mq.kafka.KafkaConsumers;
import tydic.framework.starter.mq.kafka.KafkaProducers;
import tydic.framework.starter.mq.listener.KafkaTopicListener;
import tydic.framework.starter.mq.listener.RedisQueueListener;
import tydic.framework.starter.mq.listener.RedisTopicListener;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@EnableConfigurationProperties(TydicMQProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
@EnableTydicCache
@Import(MQBuilderInvoker.class)
public class TydicMQAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {


    @Bean
    public KafkaProducers kafkaProducers(TydicMQProperties properties) {
        KafkaProducers kafkaProducers = new KafkaProducers();
        kafkaProducers.init(properties);
        return kafkaProducers;
    }

    @Bean
    public KafkaConsumers kafkaConsumers(TydicMQProperties properties) {
        KafkaConsumers kafkaConsumers = new KafkaConsumers();
        kafkaConsumers.init(properties);
        return kafkaConsumers;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SpringUtil.getMethodByAnnotation(MQListener.class)
                  .forEach(this::initMQListener);
    }

    private void initMQListener(Object bean, Map<Method, MQListener> mqListenerMap) {
        mqListenerMap.forEach((method, mqListener) -> this.initMQListener(bean, method, mqListener));
    }

    private void initMQListener(Object bean, Method method, MQListener mqListener) {
        MQMethodDetail methodDetail = this.getMQMethodDetail(method);
        if (methodDetail == null) {
            return;
        }
        if (methodDetail.isRedis() && methodDetail.isQueue()) {
            this.initRedisQueueListener(bean, method, mqListener, methodDetail);
        }
        if (methodDetail.isRedis() && methodDetail.isTopic()) {
            this.initRedisTopicListener(bean, method, mqListener, methodDetail);
        }
        if (methodDetail.isKafka() && methodDetail.isTopic()) {
            this.initKafkaTopicListener(bean, method, mqListener, methodDetail);
        }
    }


    private void initRedisQueueListener(Object bean, Method method, MQListener mqListener, MQMethodDetail methodDetail) {
        String queueName = methodDetail.getQueue().name();

        RQueue<Object> queue = SpringUtil.getAndCache(RedissonClients.class)
                                         .get(methodDetail.getQueue().source())
                                         .getQueue(queueName);
        ThreadPoolTaskScheduler scheduler = SpringUtil.getScheduler();
        for (int concurrent = 0; concurrent < mqListener.concurrency(); concurrent++) {
            RedisQueueListener listener = RedisQueueListener.builder()
                                                            .bean(bean)
                                                            .method(method)
                                                            .mqListener(mqListener)
                                                            .methodDetail(methodDetail)
                                                            .queue(queue)
                                                            .build()
                                                            .init();
            long delay = mqListener.timeUnit().toMillis(mqListener.delay());
            scheduler.scheduleWithFixedDelay(listener, delay);
        }
    }

    private void initRedisTopicListener(Object bean, Method method, MQListener mqListener, MQMethodDetail methodDetail) {
        String topicName = methodDetail.getTopic().name();
        RedissonClient redissonClient = SpringUtil.getAndCache(RedissonClients.class)
                                                  .get(methodDetail.getTopic().source());
        RedisTopicListener listener = RedisTopicListener.builder()
                                                        .bean(bean)
                                                        .method(method)
                                                        .mqListener(mqListener)
                                                        .methodDetail(methodDetail)
                                                        .build()
                                                        .init();
        redissonClient.getTopic(topicName).addListener(Object.class, listener);
    }

    private void initKafkaTopicListener(Object bean, Method method, MQListener mqListener, MQMethodDetail methodDetail) {
        ThreadPoolTaskScheduler scheduler = SpringUtil.getScheduler();
        for (int concurrent = 0; concurrent < mqListener.concurrency(); concurrent++) {
            KafkaConsumer<String, byte[]> consumer = SpringUtil.getAndCache(KafkaConsumers.class)
                                                               .create(methodDetail.getTopic().source());
            KafkaTopicListener listener = KafkaTopicListener.builder()
                                                            .bean(bean)
                                                            .method(method)
                                                            .mqListener(mqListener)
                                                            .methodDetail(methodDetail)
                                                            .consumer(consumer)
                                                            .build()
                                                            .init();
            long delay = mqListener.timeUnit().toMillis(mqListener.delay());
            scheduler.scheduleWithFixedDelay(listener, delay);
        }
    }

    private MQMethodDetail getMQMethodDetail(Method method) {
        Class<?> mqInterface = Arrays.stream(method.getDeclaringClass().getInterfaces())
                                     .filter(it -> ArrayUtil.contains(it.getInterfaces(), MQ.class))
                                     .findAny()
                                     .orElse(null);
        if (mqInterface == null) {
            return null;
        }
        Method mqMethod = ReflectUtil.getMethod(mqInterface, method.getName(), method.getParameterTypes());
        return new MQMethodDetail(mqMethod);
    }


}
