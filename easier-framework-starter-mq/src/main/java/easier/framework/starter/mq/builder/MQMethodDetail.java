package easier.framework.starter.mq.builder;

import cn.hutool.core.annotation.AnnotationUtil;
import easier.framework.core.plugin.mq.annotation.EasierQueue;
import easier.framework.core.plugin.mq.annotation.Topic;
import easier.framework.core.plugin.mq.enums.MQType;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class MQMethodDetail {
    private final Method method;
    private final EasierQueue queue;
    private final Topic topic;


    public MQMethodDetail(Method method) {
        this.method = method;
        this.queue = AnnotationUtil.getAnnotation(method, EasierQueue.class);
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
