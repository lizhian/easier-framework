package tydic.framework.starter.logging.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.plumelog.core.constant.LogMessageConstant;
import com.plumelog.core.dto.BaseLogMessage;
import com.plumelog.core.dto.RunLogMessage;
import com.plumelog.core.util.GfJsonUtil;
import com.plumelog.logback.util.LogMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.InitializingBean;
import tydic.framework.core.plugin.cache.RedisSources;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.cache.redis.RedissonClients;

import java.time.Duration;

/**
 * 使用 Redisson 推送 plume-log 日志
 */
@Slf4j
public class RedissonAppender extends AppenderBase<ILoggingEvent> implements InitializingBean {
    private static final Codec codec = new StringCodec();
    private static final int queueMaxSize = 50000;
    private static final Duration delay = Duration.ofMinutes(1);


    private volatile static boolean running = false;
    private volatile static String env = "default";
    private volatile static String applicationName;
    private volatile static RedissonClient client;

    @Override
    public void afterPropertiesSet() {
        RedissonClients redissonClients = SpringUtil.getAndCache(RedissonClients.class);
        if (redissonClients == null) {
            return;
        }
        if (redissonClients.notHas(RedisSources.plumeLog)) {
            return;
        }
        RedissonAppender.client = redissonClients.getClient(RedisSources.plumeLog);
        String activeProfile = SpringUtil.getActiveProfile();
        if (StrUtil.isNotBlank(activeProfile)) {
            RedissonAppender.env = activeProfile;
        }
        RedissonAppender.applicationName = SpringUtil.getApplicationName();
        RedissonAppender.running = true;
        SpringUtil.getScheduler()
                  .scheduleWithFixedDelay(this::checkQueueSize, delay);
    }

    private void checkQueueSize() {
        int size = RedissonAppender.client.getQueue(LogMessageConstant.LOG_KEY, codec).size();
        if (size > queueMaxSize) {
            RedissonAppender.running = false;
            log.warn("{} 堆积消息 {} 条,暂停推送日志", LogMessageConstant.LOG_KEY, size);
        } else {
            RedissonAppender.running = true;
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (event != null && RedissonAppender.running) {
            final BaseLogMessage logMessage = LogMessageUtil.getLogMessage(RedissonAppender.applicationName, RedissonAppender.env, event, null);
            if (logMessage instanceof RunLogMessage) {
                final String message = LogMessageUtil.getLogMessage(logMessage, event);
                RedissonAppender.client.getQueue(LogMessageConstant.LOG_KEY, codec)
                                       .addAsync(message);
            } else {
                RedissonAppender.client.getQueue(LogMessageConstant.LOG_KEY, codec)
                                       .addAsync(GfJsonUtil.toJSONString(logMessage));
            }
        }
    }
}
