package easier.framework.starter.job.corn;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.plugin.job.CornJob;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.job.center.JobManager;
import easier.framework.starter.job.center.RunningJob;
import lombok.SneakyThrows;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@FieldNameConstants
@EnableSpringUtil
@Configuration(proxyBeanMethods = false)
public class CornJobAutoConfiguration implements ApplicationListener<ApplicationReadyEvent>, DisposableBean {

    private final List<RunningJob> jobs = new ArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SpringUtil.getMethodByAnnotation(CornJob.class)
                .forEach(this::initCornJob);
    }

    private void initCornJob(Object bean, Map<Method, CornJob> cornJobMap) {
        cornJobMap.forEach((method, cornJob) -> this.initCornJob(bean, method, cornJob));
    }

    private void initCornJob(Object bean, Method method, CornJob cornJob) {
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
