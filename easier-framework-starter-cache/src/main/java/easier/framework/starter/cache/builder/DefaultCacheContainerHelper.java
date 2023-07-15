package easier.framework.starter.cache.builder;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.plugin.cache.container.CacheContainerHelper;
import easier.framework.starter.cache.redis.RedissonClients;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 默认缓存容器助手
 *
 * @author lizhian
 * @date 2023年07月15日
 */
@RequiredArgsConstructor
public class DefaultCacheContainerHelper implements CacheContainerHelper {
    private final RedissonClients redissonClients;

    @Override
    public long size(String source, String pattern) {
        RedissonClient client = this.redissonClients.getClient(source);
        Iterable<String> keys = client.getKeys().getKeysByPattern(pattern);
        return CollUtil.size(keys);
    }

    @Override
    public boolean has(String source, String key) {
        RedissonClient client = this.redissonClients.getClient(source);
        return client.getKeys().countExists(key) > 0;
    }

    @Override
    public Object get(String source, String key) {
        RedissonClient client = this.redissonClients.getClient(source);
        return client.getBucket(key).get();
    }

    @Override
    public void update(String source, String key, Object value) {
        RedissonClient client = this.redissonClients.getClient(source);
        client.getBucket(key).setAsync(value);
    }

    @Override
    public void update(String source, String key, Object value, Duration timeToLive) {
        RedissonClient client = this.redissonClients.getClient(source);
        client.getBucket(key).setAsync(value, timeToLive.getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void clean(String source, String key) {
        RedissonClient client = this.redissonClients.getClient(source);
        client.getBucket(key).deleteAsync();
    }

    @Override
    public void cleanAll(String source, String pattern) {
        RedissonClient client = this.redissonClients.getClient(source);
        client.getKeys().deleteByPatternAsync(pattern);
    }
}
