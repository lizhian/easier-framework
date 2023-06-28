package easier.framework.starter.mq.builder;

import cn.hutool.core.lang.Singleton;
import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.plugin.mq.MQBuilder;
import easier.framework.core.plugin.mq.MQBuilderException;
import easier.framework.core.plugin.mq.annotation.Topic;
import easier.framework.core.util.DefaultMethodUtil;
import easier.framework.core.util.InstanceUtil;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.cache.builder.CacheBuilderInvoker;
import easier.framework.starter.cache.redis.RedissonClients;
import easier.framework.starter.mq.kafka.KafkaProducers;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.redisson.api.RQueue;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class MQBuilderInvoker implements MQBuilder.Invoker {


    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {

        MQMethodDetail methodDetail = InstanceUtil.in(CacheBuilderInvoker.class)
                                                  .getInstance(method, MQMethodDetail::new);
        if (methodDetail.isRedis() && methodDetail.isQueue()) {
            this.sendRedisQueue(methodDetail, args);
        }
        if (methodDetail.isRedis() && methodDetail.isTopic()) {
            this.sendRedisTopic(methodDetail, args);
        }
        if (methodDetail.isKafka() && methodDetail.isTopic()) {
            this.sendKafkaTopic(methodDetail, args);
        }
        if (method.isDefault()) {
            return DefaultMethodUtil.invoke(proxy, method, args);
        }
        return true;
    }

    private void sendRedisQueue(MQMethodDetail methodDetail, Object[] args) {
        String queueName = methodDetail.getQueue().name();
        RQueue<Object> queue = SpringUtil.getAndCache(RedissonClients.class)
                .getClient(methodDetail.getQueue().source())
                                         .getQueue(queueName);
        int max = methodDetail.getQueue().max();
        if (max > 1 && queue.size() >= max) {
            throw MQBuilderException.of("Redis队列【{}】超过最大限制【{}】", queueName, max);
        }
        queue.offerAsync(args[0]);
    }

    private void sendRedisTopic(MQMethodDetail methodDetail, Object[] args) {
        String topicName = methodDetail.getTopic().name();
        RedissonClient redissonClient = SpringUtil.getAndCache(RedissonClients.class)
                .getClient(methodDetail.getTopic().source());
        RTopic topic = redissonClient.getTopic(topicName);
        topic.publishAsync(args[0]);
    }

    private void sendKafkaTopic(MQMethodDetail methodDetail, Object[] args) {
        Topic topic = methodDetail.getTopic();
        Codec codec = Singleton.get(topic.codec());
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic.name(), codec.serialize(args[0]));
        SpringUtil.getAndCache(KafkaProducers.class)
                  .get(topic.source())
                  .send(record);

    }


}
