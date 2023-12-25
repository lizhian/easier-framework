package tydic.user.center.application;

import easier.framework.application.web.EasierWebApplication;
import easier.framework.core.plugin.job.LoopJob;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.cache.redis.RedissonClients;
import easier.framework.starter.mybatis.EnableEasierMybatis;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.api.redisnode.RedisNode;
import org.redisson.api.redisnode.RedisNodes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
@EasierWebApplication
@EnableEasierMybatis
@MapperScan(annotationClass = Mapper.class)
// @EnableAutoTable(activeProfile = SpringUtil.dev)
public class EasierFrameworkTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasierFrameworkTestApplication.class, args);
        RedissonClients clients = SpringUtil.getBean(RedissonClients.class);
        Map<String, String> info = clients.getClient().getRedisNodes(RedisNodes.SINGLE).getInstance().info(RedisNode.InfoSection.ALL);

        log.info(info.toString());

    }

    @LoopJob(delay = 10, timeUnit = TimeUnit.SECONDS)
    public void run() {


    }
}
