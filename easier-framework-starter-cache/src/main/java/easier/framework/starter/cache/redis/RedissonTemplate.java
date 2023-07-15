package easier.framework.starter.cache.redis;


import lombok.Getter;
import lombok.experimental.Delegate;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RedissonTemplate {
    @Getter
    @Delegate
    private final RedissonClient redissonClient;

    public RedissonTemplate(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public <T> void setValue(final String key, final T value) {
        this.redissonClient.getBucket(key).set(value);
    }

    public <T> void setValue(final String key, final T value, final Duration timeout) {
        this.redissonClient.getBucket(key).set(value, timeout.getSeconds(), TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(final String key) {
        Object value = this.redissonClient.getBucket(key).get();
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public boolean delete(final String key) {
        return this.redissonClient.getBucket(key).delete();
    }

    public boolean expire(final String key, final Duration timeout) {
        return this.redissonClient.getBucket(key).expire(timeout);
    }


    /**
     * 剩余有效时间
     * 单位毫秒
     * -2 = key不存在
     * -1 = key不过期
     */
    public long remainTimeToLive(final String key) {
        return this.redissonClient.getBucket(key).remainTimeToLive();
    }

    public Iterable<String> keys(final String pattern) {
        return this.redissonClient.getKeys().getKeysByPattern(pattern);
    }

    public boolean hasKey(final String key) {
        return this.redissonClient.getKeys().countExists(key) > 0;
    }

}
