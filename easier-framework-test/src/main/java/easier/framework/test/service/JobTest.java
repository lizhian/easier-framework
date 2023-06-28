package easier.framework.test.service;

import cn.hutool.core.date.DateTime;
import easier.framework.core.plugin.job.LoopJob;
import easier.framework.starter.job.loop.LoopJobContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class JobTest {

    @LoopJob(delay = 2, timeUnit = TimeUnit.SECONDS, concurrency = 10)
    public void test() {
        log.info("{},{}", DateTime.now(), LoopJobContext.getConcurrent());
    }
}
