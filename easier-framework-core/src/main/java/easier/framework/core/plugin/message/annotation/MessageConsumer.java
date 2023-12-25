package easier.framework.core.plugin.message.annotation;

import easier.framework.core.plugin.message.fallback.MessageConsumerFallback;
import easier.framework.core.plugin.message.fallback.MessageConsumerLoggingFallback;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * 标记为消息消费者
 *
 * @author lizhian
 * @date 2023年11月24日
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MessageConsumer {

    /**
     * 是否启用
     */
    boolean enable() default true;

    /**
     * 消费并发数
     */
    int concurrency() default 1;

    /**
     * 拉模式下,一次拉取数量
     */
    int take() default 10;

    /**
     * 拉模式下,无数据情况拉取间隔
     */
    int delay() default 100;

    /**
     * 拉模式下,无数据情况拉取间隔
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;


    /**
     * 异常处理
     */
    Class<? extends MessageConsumerFallback> fallback() default MessageConsumerLoggingFallback.class;
}
