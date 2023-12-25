package easier.framework.starter.mq;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.plugin.mq.MQ;
import easier.framework.core.plugin.mq.annotation.MQListener;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.cache.EnableEasierCache;
import easier.framework.starter.cache.redis.RedissonClients;
import easier.framework.starter.mq.builder.MQBuilderInvoker;
import easier.framework.starter.mq.builder.MQMethodDetail;
import easier.framework.starter.mq.kafka.KafkaConsumers;
import easier.framework.starter.mq.kafka.KafkaProducers;
import easier.framework.starter.mq.listener.KafkaTopicListener;
import easier.framework.starter.mq.listener.RedisQueueListener;
import easier.framework.starter.mq.listener.RedisTopicListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@EnableConfigurationProperties(EasierMQProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
@EnableEasierCache
@Import(MQBuilderInvoker.class)
public class EasierMQAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {


    @Bean
    public KafkaProducers kafkaProducers(EasierMQProperties properties) {
        KafkaProducers kafkaProducers = new KafkaProducers();
        kafkaProducers.init(properties);
        return kafkaProducers;
    }

    @Bean
    public KafkaConsumers kafkaConsumers(EasierMQProperties properties) {
        KafkaConsumers kafkaConsumers = new KafkaConsumers();
        kafkaConsumers.init(properties);
        return kafkaConsumers;
    }


    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
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
                .getClient(methodDetail.getQueue().source())
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
                .getClient(methodDetail.getTopic().source());
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
