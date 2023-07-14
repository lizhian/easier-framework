package easier.framework.starter.cache.builder;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.plugin.cache.newCache.CacheHandlerInvoker;
import easier.framework.starter.cache.redis.RedissonClients;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class DefaultCacheHandlerHelper implements CacheHandlerInvoker {
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
    public boolean set(String source, String key, Object value) {
        RedissonClient client = this.redissonClients.getClient(source);
        client.getBucket(key).setAsync(value);
        return true;
    }

    @Override
    public boolean set(String source, String key, Object value, Duration timeToLive) {
        RedissonClient client = this.redissonClients.getClient(source);
        client.getBucket(key).setAsync(value, timeToLive.getSeconds(), TimeUnit.SECONDS);
        return true;
    }

    @Override
    public boolean delete(String source, String key) {
        RedissonClient client = this.redissonClients.getClient(source);
        client.getBucket(key).deleteAsync();
        return true;
    }

    @Override
    public boolean deleteAll(String source, String pattern) {
        RedissonClient client = this.redissonClients.getClient(source);
        client.getKeys().deleteByPatternAsync(pattern);
        return true;
    }

}
