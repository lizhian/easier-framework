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
import tydic.framework.core.plugin.job.LoopJob;
import tydic.framework.starter.cache.EnableTydicCache;
import tydic.framework.starter.job.corn.CornJobAutoConfiguration;
import tydic.framework.starter.job.loop.LoopJobAutoConfiguration;
import tydic.framework.starter.job.loop.LoopJobContext;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
@EnableTydicCache
@Import({
        LoopJobAutoConfiguration.class
        , CornJobAutoConfiguration.class
})
@EnableScheduling
@EnableAsync
public class TydicJobAutoConfiguration {

    @Bean
    public TaskSchedulerCustomizer taskSchedulerCustomizer() {
        return taskScheduler -> {
            Integer cpuNum = OshiUtil.getCpuInfo().getCpuNum();
            int poolSize = 2 * cpuNum + 1;
            taskScheduler.setPoolSize(poolSize);
            taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
            taskScheduler.setRejectedExecutionHandler((r, executor) ->
                    log.error("taskScheduler超过最大任务限制【ActiveCount:{}】【QueueSize:{}】,放弃任务 {}"
                            , executor.getActiveCount()
                            , executor.getQueue().size()
                            , r.toString()
                    )
            );
        };
    }


    @Bean
    public TaskExecutorCustomizer taskExecutorCustomizer() {
        return taskExecutor -> {
            Integer cpuNum = OshiUtil.getCpuInfo().getCpuNum();
            int poolSize = 2 * cpuNum + 1;
            taskExecutor.setCorePoolSize(poolSize);
            taskExecutor.setMaxPoolSize(poolSize);
            taskExecutor.setQueueCapacity(2 * poolSize);
            taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
            taskExecutor.setRejectedExecutionHandler((r, executor) ->
                    log.error("taskExecutor超过最大任务限制【ActiveCount:{}】【QueueSize:{}】,放弃任务 {}"
                            , executor.getActiveCount()
                            , executor.getQueue().size()
                            , r.toString()
                    )
            );
        };
    }


    @LoopJob(delay = 1, timeUnit = TimeUnit.SECONDS)
    public void test() {
        //当前任务的上下文
        LoopJobContext loopJobContext = LoopJobContext.get();
        LoopJob loopJob = loopJobContext.getLoopJob();
        Method method = loopJobContext.getMethod();

        //动态修改下次任务间隔
        LoopJobContext.setNextDelay(10);

        //当前任务实例
        int concurrent = LoopJobContext.getConcurrent();
    }
}
