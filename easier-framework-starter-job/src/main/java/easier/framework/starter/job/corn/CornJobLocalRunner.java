package easier.framework.starter.job.corn;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import easier.framework.core.Easier;
import easier.framework.core.plugin.job.CornJob;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronTrigger;

import java.lang.reflect.Method;

@Slf4j
@Builder
public class CornJobLocalRunner implements Runnable {
    private final Object bean;
    private final Method method;
    private final CornJob cornJob;
    private final int concurrent;

    @Getter
    private CronTrigger cronTrigger;

    public CornJobLocalRunner init() {
        String expression = StrUtil.format(this.cornJob.corn()
                , new Dict().set("second", this.cornJob.second())
                            .set("minute", this.cornJob.minute())
                            .set("hour", this.cornJob.hour())
                            .set("day", this.cornJob.day())
        );
        this.cronTrigger = new CronTrigger(expression);
        this.method.setAccessible(true);
        return this;
    }

    @SneakyThrows
    @Override
    public void run() {
        CornJobContext context = CornJobContext.builder()
                                               .bean(this.bean)
                                               .method(this.method)
                                               .cornJob(this.cornJob)
                                               .concurrent(this.concurrent)
                                               .build();
        CornJobContext.threadLocal.set(context);
        Easier.TraceId.reset();
        try {
            this.method.invoke(this.bean);
        } finally {
            CornJobContext.threadLocal.remove();
        }
    }
}
