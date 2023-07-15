package easier.framework.starter.cache.builder;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import easier.framework.core.plugin.cache.annotation.*;
import easier.framework.core.plugin.cache.enums.LocalCache;
import easier.framework.core.util.ClassUtil;
import easier.framework.core.util.DefaultMethodUtil;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.cache.redis.RedissonClients;
import lombok.Getter;
import lombok.SneakyThrows;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存方法详情
 */
@Getter
@Deprecated
public class CacheMethodDetail {
    private final Method method;
    private boolean get;
    private boolean update;
    private boolean delete;
    private boolean deleteAll;
    private final List<Integer> keyParameters = new ArrayList<>();
    private String source;
    private String name;
    private String key;
    private long timeToLive;
    private TimeUnit timeUnit;
    private int valueParameter = -1;
    private int timeToLiveParameter = -1;
    private LocalCache localCache = LocalCache.not;


    public CacheMethodDetail(Method method) {
        this.method = method;
        CacheGet cacheGet = AnnotationUtil.getAnnotation(method, CacheGet.class);
        if (cacheGet != null) {
            this.get = true;
            this.name = this.parseName(cacheGet.name(), method);
            this.key = cacheGet.key();
            this.timeToLive = cacheGet.timeToLive();
            this.timeUnit = cacheGet.timeUnit();
            this.localCache = cacheGet.localCache();
            this.source = cacheGet.source();
        }

        CacheUpdate cacheUpdate = AnnotationUtil.getAnnotation(method, CacheUpdate.class);
        if (cacheUpdate != null) {
            this.update = true;
            this.name = this.parseName(cacheUpdate.name(), method);
            this.key = cacheUpdate.key();
            this.timeToLive = cacheUpdate.timeToLive();
            this.timeUnit = cacheUpdate.timeUnit();
            this.localCache = cacheUpdate.localCache();
            this.source = cacheUpdate.source();
        }

        CacheDelete cacheDelete = AnnotationUtil.getAnnotation(method, CacheDelete.class);
        if (cacheDelete != null) {
            this.delete = true;
            this.deleteAll = cacheDelete.deleteAll();
            this.name = this.parseName(cacheDelete.name(), method);
            this.key = cacheDelete.key();
            this.source = cacheDelete.source();
        }

        //遍历入参
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            //如果有@CacheValue,记录参数序号
            if (AnnotationUtil.hasAnnotation(parameter, CacheValue.class)) {
                this.valueParameter = i;
            }
            //如果有@CacheTimeToLive,记录参数序号
            if (AnnotationUtil.hasAnnotation(parameter, CacheTimeToLive.class)) {
                this.timeToLiveParameter = i;
            }
            //如果key有使用到,记录参数序号
            if (StrUtil.contains(this.key, "{" + i + "}")) {
                this.keyParameters.add(i);
            }
        }
    }

    private String parseName(String name, Method method) {
        if (StrUtil.isNotBlank(name)) {
            return name;
        }
        return "Cache:"
                + ClassUtil.shortClassName(method.getDeclaringClass())
                + ":" + method.getName();
    }

    public String parseCacheKey(Object[] args) {
        Dict dict = Dict.create();
        for (Integer index : this.keyParameters) {
            Object arg = args[index];
            dict.put(String.valueOf(index), arg == null ? "" : arg.toString());
        }
        String key = StrUtil.format(this.key, dict);
        if (StrUtil.isNotBlank(this.name)) {
            return this.name + ":" + key;
        }
        return key;
    }

    /*
     *得到真实CacheValue,优先使用@CacheValue,如果无@CacheValue或者为null,使用default方法
     */
    @SneakyThrows
    public Object getCacheValue(Object proxy, Object[] args) {
        Object cacheValue = null;
        if (this.valueParameter > -1) {
            cacheValue = args[this.valueParameter];
        }
        if (cacheValue != null) {
            return cacheValue;
        }
        if (this.method.isDefault()) {
            cacheValue = DefaultMethodUtil.invoke(proxy, this.method, args);
        }
        return cacheValue;
    }

    public RedissonClient getRedissonClient() {
        RedissonClients clients = SpringUtil.getAndCache(RedissonClients.class);
        return clients.getClient(this.source);
    }

    public long getTimeToLive(Object[] args) {
        if (this.timeToLiveParameter < 0) {
            return this.timeToLive;
        }
        String timeToLiveValue = args[this.timeToLiveParameter].toString();
        return NumberUtil.parseNumber(timeToLiveValue).longValue();
    }
}
