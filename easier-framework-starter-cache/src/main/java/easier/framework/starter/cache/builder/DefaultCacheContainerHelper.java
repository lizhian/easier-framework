package easier.framework.starter.cache.builder;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.plugin.cache.container.CacheContainerHelper;
import easier.framework.starter.cache.redis.RedissonClients;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
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
    public long size(String source, String pattern) {
        RedissonClient client = this.redissonClients.getClient(source);
        Iterable<String> keys = client.getKeys().getKeysByPattern(pattern);
        return CollUtil.size(keys);
    }

    @Override
    public void cleanAll(String source, String pattern) {
        RedissonClient client = this.redissonClients.getClient(source);
        client.getKeys().deleteByPatternAsync(pattern);
    }

    @Override
    public List<String> keys(String source, String pattern) {
        RedissonClient client = this.redissonClients.getClient(source);
        Iterable<String> keys = client.getKeys().getKeysByPattern(pattern);
        return CollUtil.newArrayList(keys);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> values(String source, String pattern) {
        RedissonClient client = this.redissonClients.getClient(source);
        Iterable<String> keys = client.getKeys().getKeysByPattern(pattern);
        String[] array = CollUtil.newArrayList(keys).toArray(new String[]{});
        Map<String, Object> stringObjectMap = client.getBuckets().get(array);
        return (List<T>) CollUtil.newArrayList(stringObjectMap.values());
    }
}
