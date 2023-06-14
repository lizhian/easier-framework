package tydic.framework.starter.cache.redis;


import lombok.Getter;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RedissonTemplate {
    @Getter
    private final RedissonClient redissonClient;

    public RedissonTemplate(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public <T> void setValue(final String key, final T value) {
        redissonClient.getBucket(key).set(value);
    }

    public <T> void setValue(final String key, final T value, final Duration timeout) {
        redissonClient.getBucket(key).set(value, timeout.getSeconds(), TimeUnit.SECONDS);
    }

    public <T> T getValue(final String key) {
        Object value = redissonClient.getBucket(key).get();
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public boolean delete(final String key) {
        return redissonClient.getBucket(key).delete();
    }

    public boolean expire(final String key, final Duration timeout) {
        return redissonClient.getBucket(key).expire(timeout);
    }


    /**
     * 剩余有效时间
     * 单位毫秒
     * -2 = key不存在
     * -1 = key不过期
     */
    public long remainTimeToLive(final String key) {
        return redissonClient.getBucket(key).remainTimeToLive();
    }

    public Iterable<String> keys(final String pattern) {
        return redissonClient.getKeys().getKeysByPattern(pattern);
    }

    public boolean has(final String key) {
        return redissonClient.getKeys().countExists(key) > 0;
    }

}
