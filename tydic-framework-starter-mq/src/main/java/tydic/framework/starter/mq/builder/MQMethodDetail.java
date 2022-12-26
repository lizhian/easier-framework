package tydic.framework.starter.mq.builder;

import cn.hutool.core.annotation.AnnotationUtil;
import lombok.Getter;
import tydic.framework.core.plugin.mq.annotation.Queue;
import tydic.framework.core.plugin.mq.annotation.Topic;
import tydic.framework.core.plugin.mq.enums.MQType;

import java.lang.reflect.Method;

@Getter
public class MQMethodDetail {
    private final Method method;
    private final Queue queue;
    private final Topic topic;


    public MQMethodDetail(Method method) {
        this.method = method;
        this.queue = AnnotationUtil.getAnnotation(method, Queue.class);
        this.topic = AnnotationUtil.getAnnotation(method, Topic.class);
    }

    public boolean isQueue() {
        return this.queue != null;
    }

    public boolean isTopic() {
        return this.topic != null;
    }

    public boolean isRedis() {
        return (this.queue != null && this.queue.type().equals(MQType.redis))
                ||
                (this.topic != null && this.topic.type().equals(MQType.redis));
    }

    public boolean isRabbit() {
        return (this.queue != null && this.queue.type().equals(MQType.rabbit))
                ||
                (this.topic != null && this.topic.type().equals(MQType.rabbit));
    }

    public boolean isKafka() {
        return (this.queue != null && this.queue.type().equals(MQType.kafka))
                ||
                (this.topic != null && this.topic.type().equals(MQType.kafka));
    }
}
