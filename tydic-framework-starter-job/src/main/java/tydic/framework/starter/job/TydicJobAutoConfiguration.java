package tydic.framework.starter.job;

import cn.hutool.extra.spring.EnableSpringUtil;
import cn.hutool.system.oshi.OshiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tydic.framework.starter.cache.EnableTydicCache;
import tydic.framework.starter.job.center.JobController;
import tydic.framework.starter.job.corn.CornJobAutoConfiguration;
import tydic.framework.starter.job.loop.LoopJobAutoConfiguration;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
@EnableTydicCache
@Import({
        JobController.class
        , LoopJobAutoConfiguration.class
        , CornJobAutoConfiguration.class
})
@EnableScheduling
@EnableAsync
public class TydicJobAutoConfiguration {

    @Bean
    public TaskExecutorCustomizer taskExecutorCustomizer() {
        return taskExecutor -> {
            Integer cpuNum = OshiUtil.getCpuInfo().getCpuNum();
            int poolSize = 2 * cpuNum + 1;
            if (taskExecutor.getPoolSize() < poolSize) {
                taskExecutor.setCorePoolSize(poolSize);
                log.info("修改【TaskExecutor】核心线程数为:{}", poolSize);
            }
        };
    }

    @Bean
    public TaskSchedulerCustomizer taskSchedulerCustomizer() {
        return taskScheduler -> {
            Integer cpuNum = OshiUtil.getCpuInfo().getCpuNum();
            int poolSize = 2 * cpuNum + 1;
            if (taskScheduler.getPoolSize() < poolSize) {
                taskScheduler.setPoolSize(poolSize);
                log.info("修改【TaskScheduler】线程数为:{}", poolSize);
            }
        };
    }


}
