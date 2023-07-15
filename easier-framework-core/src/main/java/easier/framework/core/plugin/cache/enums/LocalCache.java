package easier.framework.core.plugin.cache.enums;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * 本机缓存类型,有效时间不同
 */
public enum LocalCache {
    //
    not(0, TimeUnit.SECONDS),
    live1s(1, TimeUnit.SECONDS),
    live2s(2, TimeUnit.SECONDS),
    live5s(5, TimeUnit.SECONDS),
    live1m(1, TimeUnit.MINUTES),
    live2m(2, TimeUnit.MINUTES),
    live5m(5, TimeUnit.MINUTES),
    live10m(10, TimeUnit.MINUTES);

    /**
     * 缓存容器
     */
    private final Cache<String, Object> cache;

    /**
     * 本地缓存存活时间
     */
    @Getter
    private final long localCacheLiveMillis;

    LocalCache(int duration, TimeUnit unit) {
        this.localCacheLiveMillis = unit.toMillis(duration);
        if (duration <= 0) {
            this.cache = null;
            return;
        }
        this.cache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(duration, unit)
                .build();
    }

    public Object get(String key) {
        if (this.cache == null) {
            return null;
        }
        if (StrUtil.isBlank(key)) {
            return null;
        }
        return this.cache.getIfPresent(key);
    }

    public void update(String key, Object value) {
        if (this.cache == null) {
            return;
        }
        if (StrUtil.isBlank(key)) {
            return;
        }
        if (value == null) {
            this.cache.invalidate(key);
            return;
        }
        this.cache.put(key, value);
    }

    public void clean(String key) {
        if (this.cache == null) {
            return;
        }
        this.cache.invalidate(key);
    }

    public void cleanAll() {
        if (this.cache == null) {
            return;
        }
        this.cache.invalidateAll();
    }
}
