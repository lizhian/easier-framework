package easier.framework.starter.job.loop;

import easier.framework.core.plugin.job.JobException;
import easier.framework.core.plugin.job.LoopJob;
import easier.framework.core.util.TraceIdUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@Builder
public class LoopJobLocalRunner implements Runnable {
    private final Object bean;
    private final Method method;
    private final LoopJob loopJob;
    private final int concurrent;

    @Getter
    private DynamicDelayTrigger trigger;

    public LoopJobLocalRunner init() {
        if (this.loopJob.delay() <= 0) {
            throw JobException.of("LoopJob.delay()不可小于1 method:{}", this.method);
        }
        this.trigger = new DynamicDelayTrigger(this.loopJob.delay());
        return this;
    }

    @SneakyThrows
    @Override
    public void run() {
        try {
            LoopJobContext context = LoopJobContext.builder()
                                                   .bean(this.bean)
                                                   .method(this.method)
                                                   .loopJob(this.loopJob)
                                                   .concurrent(this.concurrent)
                                                   .build();
            LoopJobContext.threadLocal.set(context);
            TraceIdUtil.create();
            this.method.invoke(this.bean);
        } catch (Exception e) {
            if (this.loopJob.delayOnException() > 0) {
                LoopJobContext.setNextDelay(this.loopJob.delayOnException());
            }
            throw e;
        } finally {
            this.trigger.changeDelay(LoopJobContext.getNextDelay(), this.loopJob.timeUnit());
            LoopJobContext.threadLocal.remove();
        }
    }
}
