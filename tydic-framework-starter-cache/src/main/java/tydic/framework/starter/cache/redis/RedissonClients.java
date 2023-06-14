package tydic.framework.starter.cache.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import tydic.framework.core.plugin.cache.CacheBuilderException;
import tydic.framework.core.plugin.cache.RedisSources;
import tydic.framework.core.util.IdUtil;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.cache.TydicCacheProperties;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static tydic.framework.starter.cache.TydicCacheProperties.Type.cluster;
import static tydic.framework.starter.cache.TydicCacheProperties.Type.sentinel;

/**
 * 动态 RedissonClient
 */
@Slf4j
public class RedissonClients implements InitializingBean, DisposableBean {
    private static final String REDIS_PROTOCOL_PREFIX = "redis://";
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";
    private final Map<String, RedissonClient> clients = new HashMap<>();
    private String primary;

    public boolean has(String source) {
        return this.clients.containsKey(source);
    }

    public boolean notHas(String source) {
        return !this.has(source);
    }

    @Nonnull
    public RedissonClient get() {
        if (StrUtil.isBlank(this.primary)) {
            throw CacheBuilderException.of("未配置主缓存源");
        }
        RedissonClient redissonClient = this.clients.get(this.primary);
        if (redissonClient == null) {
            throw CacheBuilderException.of("未找到缓存源:{}", this.primary);
        }
        return redissonClient;
    }


    @Nonnull
    public RedissonClient get(String source) {
        if (StrUtil.isBlank(source)) {
            return this.get();
        }
        RedissonClient redissonClient = this.clients.get(source);
        if (redissonClient == null) {
            throw CacheBuilderException.of("未找到缓存源:{}", source);
        }
        return redissonClient;
    }

    public RedissonTemplate getTemplate() {
        return new RedissonTemplate(this.get());
    }

    public RedissonTemplate getTemplate(String source) {
        return new RedissonTemplate(this.get(source));

    }

    @Override
    public void destroy() {
        this.clients.values().forEach(RedissonClient::shutdown);
    }

    @Override
    public void afterPropertiesSet() {
        SpringUtil.getExecutor().submit(this::resetWorkerID);
    }

    @SneakyThrows
    private void resetWorkerID() {
        if (this.notHas(RedisSources.snowflake)) {
            log.warn("未配置【snowflake】缓存源,【IdUtil】生成的主键可能会重复");
            return;
        }
        RedissonClient client = this.get(RedisSources.snowflake);
        for (int dataCenterId = 0; dataCenterId < 32; dataCenterId++) {
            for (int workerId = 0; workerId < 32; workerId++) {
                String snowflakeId = "Snowflake:" + dataCenterId + ":" + workerId;
                RLock snowflakeIdLock = client.getLock(snowflakeId);
                if (snowflakeIdLock.tryLock(0, TimeUnit.MINUTES)) {
                    IdUtil.reset(workerId, dataCenterId);
                    log.info("【IdUtil】全局唯一主键初始化完成, workerId= {}, dataCenterId= {}", workerId, dataCenterId);
                    return;
                }
            }
        }
    }


    public void init(TydicCacheProperties tydicCacheProperties) {
        List<String> enableRedis = tydic.framework.core.util.StrUtil.smartSplit(tydicCacheProperties.getEnableRedis());
        if (CollUtil.isEmpty(enableRedis)) {
            return;
        }
        this.primary = CollUtil.getFirst(enableRedis);
        for (String source : enableRedis) {
            TydicCacheProperties.RedissonProperties redissonProperties = tydicCacheProperties.getRedis().get(source);
            if (redissonProperties == null) {
                throw CacheBuilderException.of("无法获取缓存源配置:{}", source);
            }
            RedissonClient redissonClient = this.createClient(redissonProperties);
            this.clients.put(source, redissonClient);
            log.info("已加载缓存源【{}】", source);
            for (String alias : StrUtil.smartSplit(redissonProperties.getAlias())) {
                this.clients.put(alias, redissonClient);
                log.info("已加载缓存源【{}】", alias);
            }
        }
    }

    private RedissonClient createClient(TydicCacheProperties.RedissonProperties redissonProperties) {
        //sentinel
        if (sentinel.equals(redissonProperties.getType())) {
            TydicCacheProperties.Sentinel sentinel = redissonProperties.getSentinel();
            Config config = new Config();
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                    .setMasterName(sentinel.getMasterName())
                    .addSentinelAddress(this.convert(sentinel.getNodes()));
            sentinelServersConfig.setDatabase(sentinel.getDatabase());
            sentinelServersConfig.setPassword(sentinel.getPassword());
            if (sentinel.getConnectTimeout() > 0) {
                sentinelServersConfig.setConnectTimeout(sentinel.getConnectTimeout());
            }
            this.customize(config);
            return Redisson.create(config);
        }
        //cluster
        if (cluster.equals(redissonProperties.getType())) {
            TydicCacheProperties.Cluster cluster = redissonProperties.getCluster();
            Config config = new Config();
            ClusterServersConfig clusterServersConfig = config.useClusterServers()
                    .addNodeAddress(this.convert(cluster.getNodes()));
            clusterServersConfig.setPassword(cluster.getPassword());
            if (cluster.getConnectTimeout() > 0) {
                clusterServersConfig.setConnectTimeout(cluster.getConnectTimeout());
            }
            this.customize(config);
            return Redisson.create(config);
        }
        //single
        TydicCacheProperties.Single single = redissonProperties.getSingle();
        Config config = new Config();
        String prefix = REDIS_PROTOCOL_PREFIX;
        if (single.isSsl()) {
            prefix = REDISS_PROTOCOL_PREFIX;
        }
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(prefix + single.getHost() + ":" + single.getPort())
                .setDatabase(single.getDatabase())
                .setPassword(single.getPassword());
        if (single.getConnectTimeout() > 0) {
            singleServerConfig.setConnectTimeout(single.getConnectTimeout());
        }
        this.customize(config);
        return Redisson.create(config);
    }

    private void customize(Config config) {
        SpringUtil.getBeansOfType(RedissonConfigCustomizer.class)
                .values()
                .forEach(customizer -> customizer.customize(config));

    }

    private String[] convert(String nodes) {
        List<String> nodeList = tydic.framework.core.util.StrUtil.smartSplit(nodes)
                .stream()
                .filter(StrUtil::isNotBlank)
                .map(node -> {
                    if (node.startsWith(REDIS_PROTOCOL_PREFIX) || node.startsWith(REDISS_PROTOCOL_PREFIX)) {
                        return node;
                    }
                    return REDIS_PROTOCOL_PREFIX + node;
                })
                .collect(Collectors.toList());
        return ArrayUtil.toArray(nodeList, String.class);
    }
}
