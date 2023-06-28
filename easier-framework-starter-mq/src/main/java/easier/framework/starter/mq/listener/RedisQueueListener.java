package easier.framework.starter.mq.listener;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.plugin.mq.annotation.MQListener;
import easier.framework.core.plugin.mq.fallback.MQListenerFallback;
import easier.framework.starter.mq.builder.MQMethodDetail;
import easier.framework.starter.mq.failback.FallbackInstance;
import lombok.Builder;
import org.redisson.api.RQueue;

import java.lang.reflect.Method;
import java.util.List;

@Builder
public class RedisQueueListener implements Runnable {

    private final Object bean;
    private final Method method;
    private final MQListener mqListener;
    private final MQMethodDetail methodDetail;
    private final RQueue<Object> queue;
    private MQListenerFallback fallback;


    public RedisQueueListener init() {
        this.fallback = FallbackInstance.get(this.mqListener.fallback());
        this.method.setAccessible(true);
        return this;
    }

    @Override
    public void run() {
        while (true) {
            List<Object> messages = this.queue.poll(this.mqListener.poll());
            if (CollUtil.isEmpty(messages)) {
                break;
            }
            messages.forEach(this::invoke);
        }
    }

    private void invoke(Object message) {
        try {
            this.method.invoke(this.bean, message);
        } catch (Exception e) {
            this.fallback.onException(message, e);
        }
    }
}
