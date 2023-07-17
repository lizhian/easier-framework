package easier.framework.starter.cache.event;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.IdUtil;
import easier.framework.core.plugin.cache.RedisSources;
import easier.framework.core.plugin.event.BroadcastEvent;
import easier.framework.core.plugin.event.BroadcastEventDetail;
import easier.framework.core.util.JacksonUtil;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.cache.redis.RedissonClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class BroadcastEventListener implements InitializingBean
        , ApplicationListener<ApplicationEvent> {

    private final Set<Class<? extends ApplicationEvent>> IGNORE_EVENT = new ConcurrentHashSet<>();

    private final String TOPIC = "Easier:BroadcastEvent";
    private final String instanceId = IdUtil.fastUUID();
    private final RedissonClients redissonClients;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.redissonClients.getClient(RedisSources.event)
                .getTopic(this.TOPIC)
                .addListener(String.class, this::broadcastEventListener);
    }

    private void broadcastEventListener(CharSequence channel, String message) {
        if (StrUtil.isBlank(message)) {
            return;
        }
        BroadcastEventDetail detail;
        try {
            detail = JacksonUtil.toBean(message, BroadcastEventDetail.class);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("广播时间转换失败", e);
            }
            return;
        }
        if (this.instanceId.equals(detail.getInstanceId())) {
            return;
        }
        ApplicationEvent applicationEvent = detail.getApplicationEvent();
        SpringUtil.getExecutor().submit(() -> SpringUtil.publishEvent(applicationEvent));
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationEvent event) {
        Class<? extends ApplicationEvent> eventClass = event.getClass();
        if (this.IGNORE_EVENT.contains(eventClass)) {
            return;
        }
        BroadcastEvent broadcastEvent = AnnotationUtil.getAnnotation(eventClass, BroadcastEvent.class);
        if (broadcastEvent == null) {
            this.IGNORE_EVENT.add(eventClass);
            return;
        }
        BroadcastEventDetail eventDetail = BroadcastEventDetail.builder()
                .instanceId(this.instanceId)
                .applicationEvent(event)
                .build();
        String message = JacksonUtil.toTypingString(eventDetail);
        this.redissonClients.getClient(RedisSources.event)
                .getTopic(this.TOPIC)
                .publish(message);

    }


}
