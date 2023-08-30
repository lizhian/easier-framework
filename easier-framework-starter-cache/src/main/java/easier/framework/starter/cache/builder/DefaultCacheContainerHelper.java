package easier.framework.starter.cache.builder;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.plugin.cache.container.CacheContainerHelper;
import easier.framework.starter.cache.redis.RedissonClients;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.ByteArrayCodec;

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
    public byte[] get(String source, String key) {
        RedissonClient client = this.redissonClients.getClient(source);
        return (byte[]) client.getBucket(key, ByteArrayCodec.INSTANCE).get();
    }

    @Override
    public void update(String source, String key, byte[] value) {
        RedissonClient client = this.redissonClients.getClient(source);
        client.getBucket(key, ByteArrayCodec.INSTANCE).setAsync(value);
    }

    @Override
    public void update(String source, String key, byte[] value, Duration timeToLive) {
        RedissonClient client = this.redissonClients.getClient(source);
        if (timeToLive == null || timeToLive.toMillis() <= 0) {
            client.getBucket(key, ByteArrayCodec.INSTANCE).setAsync(value);
        } else {
            client.getBucket(key, ByteArrayCodec.INSTANCE).setAsync(value, timeToLive.getSeconds(), TimeUnit.SECONDS);
        }
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

    @Override
    public List<byte[]> values(String source, String pattern) {
        RedissonClient client = this.redissonClients.getClient(source);
        Iterable<String> keys = client.getKeys().getKeysByPattern(pattern);
        String[] array = CollUtil.newArrayList(keys).toArray(new String[]{});
        Map<String, byte[]> stringObjectMap = client.getBuckets(ByteArrayCodec.INSTANCE).get(array);
        return CollUtil.newArrayList(stringObjectMap.values());
    }
}
