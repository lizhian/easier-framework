package tydic.framework.core.plugin.job;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LoopJob {
    /**
     * 延时间隔
     */
    long delay();

    /**
     * 任务抛出异常时的延时间隔
     * 大于零的时候生效
     */
    long delayOnException() default -1;

    /**
     * 延时间隔单位
     */
    TimeUnit timeUnit();

    /**
     * 是否加锁
     */
    boolean lock() default true;

    /**
     * 任务并发数
     */
    int concurrency() default 1;
}