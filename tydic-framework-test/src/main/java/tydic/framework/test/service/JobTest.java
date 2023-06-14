package tydic.framework.test.service;

import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import tydic.framework.core.plugin.job.LoopJob;
import tydic.framework.starter.job.loop.LoopJobContext;

import java.util.concurrent.TimeUnit;

@Slf4j
public class JobTest {

    @LoopJob(delay = 2, timeUnit = TimeUnit.SECONDS, concurrency = 10)
    public void test() {
        log.info("{},{}", DateTime.now(), LoopJobContext.getConcurrent());
    }
}
