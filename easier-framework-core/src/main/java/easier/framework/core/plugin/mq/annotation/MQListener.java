package easier.framework.core.plugin.mq.annotation;

import easier.framework.core.plugin.mq.fallback.MQListenerFallback;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MQListener {

    /**
     * 拉模式下,无数据情况延时执行
     */
    int delay() default 100;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    int concurrency() default 1;

    int poll() default 10;

    Class<? extends MQListenerFallback> fallback();
}
