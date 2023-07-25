package easier.framework.starter.job.loop;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.plugin.job.LoopJob;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.job.center.JobManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

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
        JobManager jobManager = SpringUtil.getAndCache(JobManager.class);
        jobManager.submit(bean, method, loopJob);
    }
}
