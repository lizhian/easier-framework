package easier.framework.starter.job.loop;

import cn.hutool.core.date.DateTime;
import easier.framework.core.plugin.job.JobException;
import easier.framework.core.plugin.job.LoopJob;
import easier.framework.core.util.ClassUtil;
import easier.framework.core.util.TraceIdUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Builder
public class LoopJobLockRunner implements Runnable {
    private final RedissonClient redissonClient;
    private final Object bean;
    private final Method method;
    private final LoopJob loopJob;
    private final int concurrent;


    private String lockKey;
    private String nextKey;
    @Getter
    private DynamicDelayTrigger trigger;

    public LoopJobLockRunner init() {
        if (this.loopJob.delay() <= 0) {
            throw JobException.of("LoopJob.delay()不可小于1 method:{}", this.method);
        }
        String prefix = "LoopJob:" + ClassUtil.shortClassName(this.method.getDeclaringClass()) + ":" + this.method.getName();
        if (this.loopJob.concurrency() > 1) {
            prefix = prefix + ":" + this.concurrent;
        }
        this.lockKey = prefix + ":Lock";
        this.nextKey = prefix + ":Next";
        this.trigger = new DynamicDelayTrigger(this.loopJob.delay());
        this.method.setAccessible(true);
        return this;
    }

    @SneakyThrows
    @Override
    public void run() {
        RLock lock = this.redissonClient.getLock(this.lockKey);
        TimeUnit timeUnit = this.loopJob.timeUnit();
        if (!lock.tryLock(0, timeUnit)) {
            return;
        }
        if (!this.afterTheNextTime()) {
            lock.unlockAsync();
            return;
        }
        try {
            LoopJobContext.threadLocal.remove();
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
            this.recordNextTime();
            lock.unlockAsync();
            this.trigger.changeDelay(LoopJobContext.getNextDelay(), timeUnit);
            LoopJobContext.threadLocal.remove();
        }
    }

    private void recordNextTime() {
        long nextDelay = LoopJobContext.getNextDelay();
        TimeUnit timeUnit = this.loopJob.timeUnit();
        long nextTime = DateTime.now().getTime() + timeUnit.toMillis(nextDelay);
        this.redissonClient.getBucket(this.nextKey)
                           .setAsync(nextTime, nextDelay, timeUnit);
    }

    private boolean afterTheNextTime() {
        Object value = this.redissonClient.getBucket(this.nextKey).get();
        if (value == null) {
            return true;
        }
        long nextTime = Long.parseLong(value.toString());
        return DateTime.now().getTime() > nextTime;
    }
}
