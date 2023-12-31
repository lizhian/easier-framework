package easier.framework.starter.job.corn;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import easier.framework.core.Easier;
import easier.framework.core.plugin.job.CornJob;
import easier.framework.core.util.ClassUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.support.CronTrigger;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Builder
public class CornJobLockRunner implements Runnable {
    private final RedissonClient redissonClient;
    private final Object bean;
    private final Method method;
    private final CornJob cornJob;
    private final int concurrent;

    private String lockKey;
    @Getter
    private CronTrigger cronTrigger;

    public CornJobLockRunner init() {
        String prefix = "Easier:CornJob:" + ClassUtil.shortClassName(this.method.getDeclaringClass()) + ":" + this.method.getName();
        if (this.cornJob.concurrency() > 1) {
            prefix = prefix + ":" + this.concurrent;
        }
        Dict params = new Dict().set("second", this.cornJob.second())
                .set("minute", this.cornJob.minute())
                .set("hour", this.cornJob.hour())
                .set("day", this.cornJob.day());
        String expression = StrUtil.format(this.cornJob.corn(), params);
        this.lockKey = prefix + ":Lock";
        this.cronTrigger = new CronTrigger(expression);
        this.method.setAccessible(true);
        return this;
    }

    @SneakyThrows
    @Override
    public void run() {
        RLock lock = this.redissonClient.getLock(this.lockKey);
        if (!lock.tryLock(0, TimeUnit.SECONDS)) {
            return;
        }
        try {
            CornJobContext context = CornJobContext.builder()
                    .bean(this.bean)
                    .method(this.method)
                    .cornJob(this.cornJob)
                    .concurrent(this.concurrent)
                    .build();
            CornJobContext.threadLocal.set(context);
            Easier.TraceId.reset();
            this.method.invoke(this.bean);
        } finally {
            lock.unlockAsync();
            CornJobContext.threadLocal.remove();
        }
    }
}
