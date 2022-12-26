package tydic.framework.starter.job.corn;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tydic.framework.core.plugin.job.CornJob;

import java.lang.reflect.Method;

@Slf4j
@Builder
@Getter
public class CornJobContext {
    static final ThreadLocal<CornJobContext> threadLocal = new TransmittableThreadLocal<>();
    private final Object bean;
    private final Method method;
    private final CornJob cornJob;
    private final int concurrent;

    /**
     * 获取任务上下文
     */
    public static CornJobContext get() {
        return threadLocal.get();
    }

    /**
     * 获取任务当前并发标记
     */
    public static int getConcurrent() {
        return get().concurrent;
    }
}
