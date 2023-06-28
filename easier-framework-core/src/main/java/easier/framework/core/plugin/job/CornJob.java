package easier.framework.core.plugin.job;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CornJob {

    /**
     * corn表达式
     * 默认【0 * * * * ?】每分钟执行一次
     */
    String corn() default "{second} {minute} {hour} {day} * ?";

    String second() default "0";

    String minute() default "*";

    String hour() default "*";

    String day() default "*";

    /**
     * 是否加锁
     */
    boolean lock() default true;

    /**
     * 任务并发数
     */
    int concurrency() default 1;
}