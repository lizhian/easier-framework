package easier.framework.core.plugin.cache.container;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import easier.framework.core.util.StrUtil;

import java.time.Duration;


class LocalCache<T> {

    /**
     * 本地缓存
     */
    private final Cache<String, T> _cache;

    LocalCache() {
        this._cache = null;
    }

    public LocalCache(int maximumSize, Duration timeToLive) {
        this._cache = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(timeToLive)
                .build();
    }

    T get(String key) {
        if (this._cache == null || StrUtil.isBlank(key)) {
            return null;
        }
        return this._cache.getIfPresent(key);
    }

    void update(String key, T value) {
        if (this._cache == null || StrUtil.isBlank(key)) {
            return;
        }
        if (value == null) {
            this._cache.invalidate(key);
            return;
        }
        this._cache.put(key, value);
    }

    void clean(String key) {
        if (this._cache == null || StrUtil.isBlank(key)) {
            return;
        }
        this._cache.invalidate(key);
    }

    void cleanAll() {
        if (this._cache == null) {
            return;
        }
        this._cache.invalidateAll();
    }
}
