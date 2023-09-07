package easier.framework.starter.cache.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import easier.framework.core.plugin.cache.RedisSources;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.util.IdUtil;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.cache.EasierCacheProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static easier.framework.starter.cache.EasierCacheProperties.Type.*;

/**
 * 动态 RedissonClient
 */
@Slf4j
public class RedissonClients implements InitializingBean, DisposableBean {
    private static final String REDIS_PROTOCOL_PREFIX = "redis://";
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";
    private final Map<String, RedissonClient> clients = new HashMap<>();
    private String primary;

    public boolean contains(String source) {
        return this.clients.containsKey(source);
    }

    @Nonnull
    public RedissonClient getClient() {
        if (StrUtil.isBlank(this.primary)) {
            throw FrameworkException.of("未配置主缓存源");
        }
        RedissonClient redissonClient = this.clients.get(this.primary);
        if (redissonClient == null) {
            throw FrameworkException.of("未找到缓存源:{}", this.primary);
        }
        return redissonClient;
    }


    @Nonnull
    public RedissonClient getClient(String source) {
        if (StrUtil.isBlank(source)) {
            return this.getClient();
        }
        RedissonClient redissonClient = this.clients.get(source);
        if (redissonClient == null) {
            throw FrameworkException.of("未找到缓存源:{}", source);
        }
        return redissonClient;
    }

    public RedissonTemplate getTemplate() {
        return new RedissonTemplate(this.getClient());
    }

    public RedissonTemplate getTemplate(String source) {
        return new RedissonTemplate(this.getClient(source));

    }

    @Override
    public void destroy() {
        this.clients.values().forEach(RedissonClient::shutdown);
        log.info("已关闭RedissonClients");
    }

    @Override
    public void afterPropertiesSet() {
        ThreadPoolTaskExecutor executor = SpringUtil.getExecutor();
        if (executor != null) {
            executor.submit(this::resetWorkerID);
            return;
        }
        this.resetWorkerID();
    }

    @SneakyThrows
    private void resetWorkerID() {
        if (!this.contains(RedisSources.snowflake)) {
            log.warn("未配置【{}】缓存源,【IdUtil】生成的主键可能会重复", RedisSources.snowflake);
            return;
        }
        RedissonClient client = this.getClient(RedisSources.snowflake);
        for (int dataCenterId = 0; dataCenterId < 31; dataCenterId++) {
            for (int workerId = 0; workerId < 31; workerId++) {
                String snowflakeId = "Easier:Snowflake:" + dataCenterId + "_" + workerId;
                RLock snowflakeIdLock = client.getLock(snowflakeId);
                if (snowflakeIdLock.tryLock()) {
                    IdUtil.reset(workerId, dataCenterId);
                    log.info("【IdUtil】全局唯一主键初始化完成, workerId= {}, dataCenterId= {}", workerId, dataCenterId);
                    return;
                }
            }
        }
    }


    public void init(EasierCacheProperties easierCacheProperties) {
        List<String> enableRedis = StrUtil.smartSplit(easierCacheProperties.getEnableRedis());
        if (CollUtil.isEmpty(enableRedis)) {
            return;
        }
        this.primary = CollUtil.getFirst(enableRedis);
        for (String source : enableRedis) {
            EasierCacheProperties.RedissonProperties properties = easierCacheProperties.getRedis().get(source);
            if (properties == null) {
                throw FrameworkException.of("无法获取缓存源配置:{}", source);
            }
            RedissonClient redissonClient = this.createClient(properties);
            this.clients.put(source, redissonClient);
            log.info("已加载缓存源【{}】", source);
            for (String alias : StrUtil.smartSplit(properties.getAlias())) {
                this.clients.put(alias, redissonClient);
                log.info("已加载缓存源【{}】", alias);
            }
        }
    }

    private RedissonClient createClient(EasierCacheProperties.RedissonProperties properties) {
        EasierCacheProperties.Type type = properties.getType();
        String nodes = properties.getNodes();
        String password = properties.getPassword();
        int database = properties.getDatabase();
        boolean ssl = properties.isSsl();
        int connectTimeoutMillis = properties.getConnectTimeoutMillis();
        String[] address = this.convert(nodes, ssl);
        Config config = new Config();
        // single
        if (single.equals(type)) {
            config.useSingleServer()
                    .setAddress(address[0])
                    .setDatabase(database)
                    .setPassword(password)
                    .setConnectTimeout(connectTimeoutMillis);
        }
        // sentinel
        if (sentinel.equals(type)) {
            config.useSentinelServers()
                    .setMasterName(properties.getSentinel().getMasterName())
                    .addSentinelAddress(address)
                    .setDatabase(database)
                    .setPassword(password)
                    .setConnectTimeout(connectTimeoutMillis);
        }
        // cluster
        if (cluster.equals(type)) {
            config.useClusterServers()
                    .addNodeAddress(address)
                    .setPassword(password)
                    .setConnectTimeout(connectTimeoutMillis);
        }
        this.customize(config);
        return Redisson.create(config);
    }

    private void customize(Config config) {
        SpringUtil.getBeansOfType(RedissonConfigCustomizer.class)
                .values()
                .forEach(customizer -> customizer.customize(config));

    }

    private String[] convert(String nodes, boolean ssl) {
        List<String> nodeList = StrUtil.splitTrim(nodes, ",")
                .stream()
                .filter(StrUtil::isNotBlank)
                .map(node -> {
                    if (node.startsWith(REDIS_PROTOCOL_PREFIX) || node.startsWith(REDISS_PROTOCOL_PREFIX)) {
                        return node;
                    }
                    return ssl ? REDISS_PROTOCOL_PREFIX : REDIS_PROTOCOL_PREFIX + node;
                })
                .collect(Collectors.toList());
        return ArrayUtil.toArray(nodeList, String.class);
    }
}
