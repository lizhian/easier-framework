package tydic.framework.starter.job.corn;

import cn.hutool.extra.spring.EnableSpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import tydic.framework.core.plugin.cache.RedisSources;
import tydic.framework.core.plugin.job.CornJob;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.cache.redis.RedissonClients;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@EnableSpringUtil
@Configuration(proxyBeanMethods = false)
public class CornJobAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SpringUtil.getMethodByAnnotation(CornJob.class)
                  .forEach(this::initCornJob);
    }

    private void initCornJob(Object bean, Map<Method, CornJob> cornJobMap) {
        cornJobMap.forEach((method, cornJob) -> this.initCornJob(bean, method, cornJob));
    }

    private void initCornJob(Object bean, Method method, CornJob cornJob) {
        for (int concurrent = 0; concurrent < cornJob.concurrency(); concurrent++) {
            this.initCornJob(bean, method, cornJob, concurrent);
        }
    }

    private void initCornJob(Object bean, Method method, CornJob cornJob, int concurrent) {
        ThreadPoolTaskScheduler scheduler = SpringUtil.getScheduler();
        //创建加锁任务
        if (cornJob.lock()) {
            RedissonClient redissonClient = SpringUtil.getAndCache(RedissonClients.class)
                                                      .get(RedisSources.job);
            CornJobLockRunner runner = CornJobLockRunner.builder()
                                                        .redissonClient(redissonClient)
                                                        .bean(bean)
                                                        .method(method)
                                                        .cornJob(cornJob)
                                                        .concurrent(concurrent)
                                                        .build()
                                                        .init();
            scheduler.schedule(runner, runner.getCronTrigger());
            return;
        }
        //创建非加锁任务
        CornJobLocalRunner runner = CornJobLocalRunner.builder()
                                                      .bean(bean)
                                                      .method(method)
                                                      .cornJob(cornJob)
                                                      .concurrent(concurrent)
                                                      .build()
                                                      .init();
        scheduler.schedule(runner, runner.getCronTrigger());
    }

}
