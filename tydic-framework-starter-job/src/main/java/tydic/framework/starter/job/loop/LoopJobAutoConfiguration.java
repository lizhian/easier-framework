package tydic.framework.starter.job.loop;

import cn.hutool.extra.spring.EnableSpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import tydic.framework.core.plugin.cache.RedisSources;
import tydic.framework.core.plugin.job.LoopJob;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.cache.redis.RedissonClients;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
public class LoopJobAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SpringUtil.getMethodByAnnotation(LoopJob.class)
                  .forEach(this::initLoopJob);
    }

    private void initLoopJob(Object bean, Map<Method, LoopJob> cornJobMap) {
        cornJobMap.forEach((method, loopJob) -> this.initLoopJob(bean, method, loopJob));
    }

    private void initLoopJob(Object bean, Method method, LoopJob loopJob) {
        for (int concurrent = 0; concurrent < loopJob.concurrency(); concurrent++) {
            this.initLoopJob(bean, method, loopJob, concurrent);
        }
    }

    private void initLoopJob(Object bean, Method method, LoopJob loopJob, int concurrent) {
        ThreadPoolTaskScheduler scheduler = SpringUtil.getScheduler();
        //创建加锁任务
        if (loopJob.lock()) {
            RedissonClient redissonClient = SpringUtil.getAndCache(RedissonClients.class)
                                                      .get(RedisSources.job);
            LoopJobLockRunner runner = LoopJobLockRunner.builder()
                                                        .redissonClient(redissonClient)
                                                        .bean(bean)
                                                        .method(method)
                                                        .loopJob(loopJob)
                                                        .concurrent(concurrent)
                                                        .build()
                                                        .init();
            scheduler.schedule(runner, runner.getTrigger());
            return;
        }

        //创建非加锁任务
        LoopJobLocalRunner runner = LoopJobLocalRunner.builder()
                                                      .bean(bean)
                                                      .method(method)
                                                      .loopJob(loopJob)
                                                      .concurrent(concurrent)
                                                      .build()
                                                      .init();
        scheduler.schedule(runner, runner.getTrigger());
    }

}
