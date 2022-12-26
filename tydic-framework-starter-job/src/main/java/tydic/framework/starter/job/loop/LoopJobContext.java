package tydic.framework.starter.job.loop;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tydic.framework.core.plugin.job.LoopJob;

import java.lang.reflect.Method;

/**
 * 任务上下文
 */
@Slf4j
@Builder
@Getter
public class LoopJobContext {
    static final ThreadLocal<LoopJobContext> threadLocal = new TransmittableThreadLocal<>();
    private final Object bean;
    private final Method method;
    private final LoopJob loopJob;
    private final int concurrent;
    private Long nextDelay;

    /**
     * 获取任务上下文
     */
    public static LoopJobContext get() {
        return threadLocal.get();
    }

    /**
     * 获取任务当前并发标记
     */
    public static int getConcurrent() {
        return get().concurrent;
    }

    /**
     * 获取下次任务的任务间隔
     */
    public static long getNextDelay() {
        LoopJobContext context = get();
        if (context.nextDelay != null) {
            return context.nextDelay;
        }
        return context.loopJob.delay();
    }

    /**
     * 配置下次任务的延时间隔
     */
    public static void setNextDelay(long delay) {
        if (delay < 1) {
            return;
        }
        LoopJobContext context = get();
        if (context == null) {
            return;
        }
        context.nextDelay = delay;
    }
}
