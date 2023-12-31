package easier.framework.starter.mq.listener;

import easier.framework.core.plugin.mq.annotation.MQListener;
import easier.framework.core.plugin.mq.fallback.MQListenerFallback;
import easier.framework.starter.mq.builder.MQMethodDetail;
import easier.framework.starter.mq.failback.FallbackInstance;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.client.RedisPubSubListener;
import org.redisson.client.protocol.pubsub.PubSubType;

import java.lang.reflect.Method;

@Slf4j
@Builder
public class RedisTopicListener implements RedisPubSubListener<Object> {

    private final Object bean;
    private final Method method;
    private final MQListener mqListener;
    private final MQMethodDetail methodDetail;
    private MQListenerFallback fallback;


    public RedisTopicListener init() {
        this.fallback = FallbackInstance.get(this.mqListener.fallback());
        this.method.setAccessible(true);
        return this;
    }


    @Override
    public void onStatus(PubSubType type, CharSequence channel) {
    }

    @Override
    public void onPatternMessage(CharSequence pattern, CharSequence channel, Object message) {
        try {
            this.method.invoke(this.bean, message);
        } catch (Exception e) {
            this.fallback.onException(message, e);
        }
    }

    @Override
    public void onMessage(CharSequence channel, Object message) {
        try {
            this.method.invoke(this.bean, message);
        } catch (Exception e) {
            this.fallback.onException(message, e);
        }
    }


}
