package tydic.framework.starter.mq.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Singleton;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import tydic.framework.core.plugin.codec.Codec;
import tydic.framework.core.plugin.mq.annotation.MQListener;
import tydic.framework.core.plugin.mq.annotation.Topic;
import tydic.framework.core.plugin.mq.fallback.MQListenerFallback;
import tydic.framework.starter.mq.builder.MQMethodDetail;
import tydic.framework.starter.mq.failback.FallbackInstance;

import java.lang.reflect.Method;
import java.time.Duration;

@Slf4j
@Builder
public class KafkaTopicListener implements Runnable {

    private final Object bean;
    private final Method method;
    private final MQListener mqListener;
    private final MQMethodDetail methodDetail;
    private final KafkaConsumer<String, byte[]> consumer;

    private MQListenerFallback fallback;


    public KafkaTopicListener init() {
        this.fallback = FallbackInstance.get(this.mqListener.fallback());
        this.method.setAccessible(true);
        String topicName = this.methodDetail.getTopic().name();
        this.consumer.subscribe(CollUtil.newArrayList(topicName));
        for (PartitionInfo partitionInfo : this.consumer.partitionsFor(topicName)) {
            log.info(partitionInfo.toString());
        }
        return this;
    }


    @Override
    public void run() {
        while (true) {
            ConsumerRecords<String, byte[]> records = this.consumer.poll(Duration.ofSeconds(1));
            if (records == null || records.isEmpty()) {
                break;
            }
            records.forEach(this::invoke);
        }
    }

    private void invoke(ConsumerRecord<String, byte[]> record) {
        try {
            Topic topic = this.methodDetail.getTopic();
            Codec codec = Singleton.get(topic.codec());
            Object message = codec.deserialize(record.value());
            this.method.invoke(this.bean, message);
        } catch (Exception e) {
            this.fallback.onException(record, e);
        }
    }
}
