package easier.framework.test.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import com.plumelog.core.constant.LogMessageConstant;
import easier.framework.core.Easier;
import easier.framework.core.plugin.cache.RedisSources;
import easier.framework.core.plugin.job.LoopJob;
import easier.framework.starter.cache.condition.ConditionalOnRedisSource;
import easier.framework.starter.cache.redis.RedissonClients;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.eo.RunningLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnRedisSource(RedisSources.logging)
public class RunningLogListener {
    private static final Codec codec = new StringCodec();
    private static final Repo<RunningLog> _running_log = Repos.of(RunningLog.class);
    private final RedissonClients redissonClients;


    /**
     * 插入运行日志
     */
    @LoopJob(delay = 100, timeUnit = TimeUnit.MILLISECONDS, concurrency = 3, lock = false)
    public void saveRunningLog() {
        try {
            Easier.TraceId.disable();
            while (true) {
                RedissonClient clientsClient = this.redissonClients.getClient(RedisSources.logging);
                RQueue<String> queue = clientsClient.getQueue(LogMessageConstant.LOG_KEY, codec);
                List<String> records = queue.poll(100);
                if (CollUtil.isEmpty(records)) {
                    break;
                }
                List<RunningLog> runningLogs = records.stream()
                        .map(record -> Easier.Json.toBean(record, RunningLog.class))
                        .collect(Collectors.toList());
                _running_log.addBatch(runningLogs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除运行日志
     */
    @LoopJob(delay = 10, timeUnit = TimeUnit.MINUTES)
    public void deleteRunningLog() {
        try {
            // 删除3个小时前的数据
            long time = DateTime.now().getTime() - Duration.ofHours(3).toMillis();
            _running_log.newUpdate()
                    .lt(RunningLog::getDtTime, time)
                    .remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
