package easier.framework.starter.job.loop;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.plugin.job.LoopJob;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.job.center.JobManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<String, List<Method>> groupingBy = cornJobMap.keySet()
                .stream()
                .collect(Collectors.groupingBy(Method::getName));
        for (String methodName : groupingBy.keySet()) {
            if (groupingBy.get(methodName).size() > 1) {
                throw FrameworkException.of("{} 包含多个相同名称的LoopJob方法:{}", bean.getClass().getSimpleName(), methodName);
            }
        }
        cornJobMap.forEach((method, loopJob) -> this.initLoopJob(bean, method, loopJob));
    }

    private void initLoopJob(Object bean, Method method, LoopJob loopJob) {
        if (method.getParameterTypes().length > 0 || !method.getReturnType().getName().equals("void")) {
            throw FrameworkException.of("LoopJob方法必须为无参无返回类型:{}", method);
        }
        JobManager jobManager = SpringUtil.getAndCache(JobManager.class);
        jobManager.submit(bean, method, loopJob);
    }
}
