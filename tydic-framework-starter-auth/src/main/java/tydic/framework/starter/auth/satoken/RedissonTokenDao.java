package tydic.framework.starter.auth.satoken;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.collection.CollUtil;
import org.redisson.api.RedissonClient;
import tydic.framework.core.plugin.cache.RedisSources;
import tydic.framework.starter.cache.redis.RedissonClients;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class RedissonTokenDao implements SaTokenDao {
    private final RedissonClient client;

    public RedissonTokenDao(RedissonClients redissonClients) {
        this.client = redissonClients.get(RedisSources.auth);
    }

    @Override
    public String get(String key) {
        Object object = this.getObject(key);
        if (object == null) {
            return null;
        }
        return object.toString();
    }

    @Override
    public void set(String key, String value, long timeout) {
        this.setObject(key, value, timeout);
    }

    @Override
    public void update(String key, String value) {
        this.updateObject(key, value);
    }

    @Override
    public void delete(String key) {
        this.deleteObject(key);
    }

    @Override
    public long getTimeout(String key) {
        return this.getObjectTimeout(key);
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        this.updateObjectTimeout(key, timeout);
    }

    @Override
    public Object getObject(String key) {
        return this.client.getBucket(key).get();
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        // 判断是否为永不过期
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            this.client.getBucket(key).setAsync(object);
        } else {
            this.client.getBucket(key).setAsync(object, timeout, TimeUnit.SECONDS);
        }
    }

    @Override
    public void updateObject(String key, Object object) {
        this.client.getBucket(key).setIfExists(object);
    }

    @Override
    public void deleteObject(String key) {
        this.client.getBucket(key).deleteAsync();
    }

    @Override
    public long getObjectTimeout(String key) {
        long timeToLive = this.client.getBucket(key).remainTimeToLive();
        if (timeToLive < 0) {
            return timeToLive;
        }
        long second = timeToLive / 1000;
        return second < 1 ? 1 : second;
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = this.getObjectTimeout(key);
            if (expire != SaTokenDao.NEVER_EXPIRE) {
                this.setObject(key, this.getObject(key), timeout);
            }
            return;
        }
        this.client.getBucket(key).expireAsync(Duration.ofSeconds(timeout));
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Iterable<String> keys = this.client.getKeys().getKeysByPattern(prefix + "*" + keyword + "*");
        List<String> list = CollUtil.newArrayList(keys);
        return SaFoxUtil.searchList(list, start, size, sortType);
    }
}
