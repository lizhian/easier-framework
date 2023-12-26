package easier.framework.starter.job.corn;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.plugin.job.CornJob;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.job.center.JobManager;
import easier.framework.starter.job.center.RunningJob;
import lombok.SneakyThrows;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@FieldNameConstants
@EnableSpringUtil
@Configuration(proxyBeanMethods = false)
public class CornJobAutoConfiguration implements ApplicationListener<ApplicationReadyEvent>, DisposableBean {

    private final List<RunningJob> jobs = new ArrayList<>();

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        SpringUtil.getMethodByAnnotation(CornJob.class)
                .forEach(this::initCornJob);
    }

    private void initCornJob(Object bean, Map<Method, CornJob> cornJobMap) {
        Map<String, List<Method>> groupingBy = cornJobMap.keySet()
                .stream()
                .collect(Collectors.groupingBy(Method::getName));
        for (String methodName : groupingBy.keySet()) {
            if (groupingBy.get(methodName).size() > 1) {
                Class<?> clazz = groupingBy.get(methodName).get(0).getDeclaringClass();
                throw FrameworkException.of("【{}】包含多个相同名称的 @CornJob 方法【{}】", clazz, groupingBy.get(methodName));
            }
        }
        cornJobMap.forEach((method, cornJob) -> this.initCornJob(bean, method, cornJob));
    }

    private void initCornJob(Object bean, Method method, CornJob cornJob) {
        if (method.getParameterTypes().length > 0 || !method.getReturnType().getName().equals("void")) {
            throw FrameworkException.of("CornJob方法必须为无参无返回类型:{}", method);
        }
        JobManager jobManager = SpringUtil.getAndCache(JobManager.class);
        RunningJob submit = jobManager.submit(bean, method, cornJob);
        this.jobs.add(submit);
    }

    @Override
    @SneakyThrows
    public void destroy() throws Exception {
        this.jobs.forEach(RunningJob::cancel);

    }
}
