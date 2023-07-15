package easier.framework.starter.cache.builder;

import cn.hutool.core.util.StrUtil;
import easier.framework.core.plugin.cache.CacheBuilder;
import easier.framework.core.plugin.cache.enums.LocalCache;
import easier.framework.core.util.DefaultMethodUtil;
import easier.framework.core.util.InstanceUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 缓存代理执行器
 */
@Slf4j
@Deprecated
public class CacheBuilderInvoker implements CacheBuilder.Invoker {

    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {
        CacheMethodDetail methodDetail = InstanceUtil.in(CacheBuilderInvoker.class)
                .getInstance(method, CacheMethodDetail::new);
        //获取缓存
        if (methodDetail.isGet()) {
            return this.invokeGet(methodDetail, proxy, args);
        }
        //更新缓存
        if (methodDetail.isUpdate()) {
            return this.invokeUpdate(methodDetail, proxy, args);
        }
        //删除缓存
        if (methodDetail.isDelete()) {
            return this.invokeDelete(methodDetail, args);
        }
        if (method.isDefault()) {
            return DefaultMethodUtil.invoke(proxy, method, args);
        }
        return null;
    }


    private Object invokeGet(CacheMethodDetail methodDetail, Object proxy, Object[] args) {
        String cacheKey = methodDetail.parseCacheKey(args);
        //1 尝试获取本地缓存
        LocalCache localCache = methodDetail.getLocalCache();
        Object localCacheValue = localCache.get(cacheKey);
        if (localCacheValue != null) {
            return localCacheValue;
        }
        //2 尝试获取redis缓存
        RedissonClient redissonClient = methodDetail.getRedissonClient();
        Object redisCacheValue = redissonClient.getBucket(cacheKey).get();
        if (redisCacheValue != null) {
            return redisCacheValue;
        }
        //3 获取缓存值
        Object newCacheValue = methodDetail.getCacheValue(proxy, args);
        //更新缓存
        long timeToLive = methodDetail.getTimeToLive(args);
        this.updateCacheValue(cacheKey, newCacheValue, timeToLive, methodDetail);
        return newCacheValue;
    }


    private boolean invokeUpdate(CacheMethodDetail methodDetail, Object proxy, Object[] args) {
        String cacheKey = methodDetail.parseCacheKey(args);
        //获取缓存值
        Object newCacheValue = methodDetail.getCacheValue(proxy, args);
        //更新缓存
        long timeToLive = methodDetail.getTimeToLive(args);
        this.updateCacheValue(cacheKey, newCacheValue, timeToLive, methodDetail);
        return true;
    }

    private boolean invokeDelete(CacheMethodDetail methodDetail, Object[] args) {
        RedissonClient redissonClient = methodDetail.getRedissonClient();
        RKeys keys = redissonClient.getKeys();
        if (methodDetail.isDeleteAll()) {
            String name = methodDetail.getName();
            return keys.deleteByPattern(name + ":*") > 0;
        }
        String cacheKey = methodDetail.parseCacheKey(args);
        return keys.delete(cacheKey) > 0;
    }


    private void updateCacheValue(String cacheKey, Object newCacheValue, long timeToLive, CacheMethodDetail methodDetail) {
        if (StrUtil.isBlank(cacheKey) || newCacheValue == null) {
            return;
        }
        LocalCache localCache = methodDetail.getLocalCache();
        RBucket<Object> redisCache = methodDetail.getRedissonClient().getBucket(cacheKey);

        //缓存不过期
        if (timeToLive <= 0) {
            redisCache.setAsync(newCacheValue);
            localCache.update(cacheKey, newCacheValue);
            return;
        }

        TimeUnit timeUnit = methodDetail.getTimeUnit();
        redisCache.setAsync(newCacheValue, timeToLive, timeUnit);
        //redis 缓存时间必须大于本地缓存时间
        if (timeUnit.toMillis(timeToLive) >= localCache.getLocalCacheLiveMillis()) {
            localCache.update(cacheKey, newCacheValue);
        } else {
            localCache.clean(cacheKey);
        }
    }
}
