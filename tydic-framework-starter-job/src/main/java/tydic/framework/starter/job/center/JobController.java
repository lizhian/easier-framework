package tydic.framework.starter.job.center;

import cn.hutool.core.annotation.AnnotationUtil;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import tydic.framework.core.function.SConsumer;
import tydic.framework.core.plugin.cache.RedisSources;
import tydic.framework.core.plugin.exception.biz.FrameworkException;
import tydic.framework.core.plugin.job.CornJob;
import tydic.framework.core.plugin.job.LoopJob;
import tydic.framework.core.util.LambdaUtil;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.cache.redis.RedissonClients;
import tydic.framework.starter.job.corn.CornJobLocalRunner;
import tydic.framework.starter.job.corn.CornJobLockRunner;
import tydic.framework.starter.job.loop.LoopJobLocalRunner;
import tydic.framework.starter.job.loop.LoopJobLockRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Job 控制器
 */
public class JobController {

    /**
     * 提交任务
     */
    public <T> RunningJob submit(T t, SConsumer<T> jobFunction) {
        Method method = LambdaUtil.getMethod(jobFunction);
        return this.submit(t, method);
    }


    /**
     * 提交任务
     */
    public RunningJob submit(Object object, Method method) {
        LoopJob loopJob = AnnotationUtil.getAnnotation(method, LoopJob.class);
        if (loopJob != null) {
            return this.submit(object, method, loopJob);
        }
        CornJob cornJob = AnnotationUtil.getAnnotation(method, CornJob.class);
        if (cornJob != null) {
            return this.submit(object, method, cornJob);
        }
        throw FrameworkException.of("方法【{}】未配置【@LoopJob】或【@CornJob】注解", method);
    }


    /**
     * 提交 LoopJob
     */
    public RunningJob submit(Object bean, Method method, LoopJob loopJob) {
        ThreadPoolTaskScheduler scheduler = SpringUtil.getScheduler();
        RedissonClient redissonClient = SpringUtil.getAndCache(RedissonClients.class)
                .get(RedisSources.job);
        List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
        for (int concurrent = 0; concurrent < loopJob.concurrency(); concurrent++) {
            if (loopJob.lock()) {
                LoopJobLockRunner runner = LoopJobLockRunner.builder()
                        .redissonClient(redissonClient)
                        .bean(bean)
                        .method(method)
                        .loopJob(loopJob)
                        .concurrent(concurrent)
                        .build()
                        .init();
                ScheduledFuture<?> scheduledFuture = scheduler.schedule(runner, runner.getTrigger());
                scheduledFutures.add(scheduledFuture);
                continue;
            }

            //创建非加锁任务
            LoopJobLocalRunner runner = LoopJobLocalRunner.builder()
                    .bean(bean)
                    .method(method)
                    .loopJob(loopJob)
                    .concurrent(concurrent)
                    .build()
                    .init();
            ScheduledFuture<?> scheduledFuture = scheduler.schedule(runner, runner.getTrigger());
            scheduledFutures.add(scheduledFuture);
        }
        return new RunningJob(scheduledFutures);
    }


    /**
     * 提交 CornJob
     */
    public RunningJob submit(Object bean, Method method, CornJob cornJob) {
        ThreadPoolTaskScheduler scheduler = SpringUtil.getScheduler();
        RedissonClient redissonClient = SpringUtil.getAndCache(RedissonClients.class)
                .get(RedisSources.job);
        List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
        for (int concurrent = 0; concurrent < cornJob.concurrency(); concurrent++) {
            //创建加锁任务
            if (cornJob.lock()) {
                CornJobLockRunner runner = CornJobLockRunner.builder()
                        .redissonClient(redissonClient)
                        .bean(bean)
                        .method(method)
                        .cornJob(cornJob)
                        .concurrent(concurrent)
                        .build()
                        .init();
                ScheduledFuture<?> scheduledFuture = scheduler.schedule(runner, runner.getCronTrigger());
                scheduledFutures.add(scheduledFuture);
                continue;
            }
            //创建非加锁任务
            CornJobLocalRunner runner = CornJobLocalRunner.builder()
                    .bean(bean)
                    .method(method)
                    .cornJob(cornJob)
                    .concurrent(concurrent)
                    .build()
                    .init();
            ScheduledFuture<?> scheduledFuture = scheduler.schedule(runner, runner.getCronTrigger());
            scheduledFutures.add(scheduledFuture);

        }
        return new RunningJob(scheduledFutures);
    }
}
