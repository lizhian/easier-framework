package easier.framework.core.plugin.cache.container;

import easier.framework.core.plugin.cache.enums.LocalCache;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.util.StrUtil;
import lombok.NonNull;

import java.time.Duration;
import java.util.function.Function;

public class CacheContainerBuilder<T> {
    /**
     * 缓存源
     */
    private String source;

    /**
     * key模板
     */
    private String keyTemplate;
    /**
     * 活着时间
     */
    private Duration timeToLive;
    /**
     * 本地缓存
     */
    private LocalCache local = LocalCache.not;

    /**
     * value类型
     */
    private Class<T> clazz;
    /**
     * 系列化记录value类型
     */
    private boolean typing;
    /**
     * value获取函数
     */
    private Function<String, T> value = key -> null;

    public CacheContainerBuilder() {
    }

    public CacheContainerBuilder(@NonNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public CacheContainerBuilder<T> source(@NonNull String source) {
        this.source = source;
        return this;
    }

    public CacheContainerBuilder<T> keyTemplate(@NonNull String keyTemplate) {
        this.keyTemplate = keyTemplate;
        return this;
    }

    public CacheContainerBuilder<T> keyPrefix(@NonNull String keyPrefix) {
        if (keyPrefix.endsWith(":")) {
            this.keyTemplate = keyPrefix + "{}";
        } else {
            this.keyTemplate = keyPrefix + ":{}";
        }
        return this;
    }

    public CacheContainerBuilder<T> timeToLive(@NonNull Duration timeToLive) {
        this.timeToLive = timeToLive;
        return this;
    }

    public CacheContainerBuilder<T> timeToLiveSeconds(long timeToLiveSeconds) {
        this.timeToLive = Duration.ofSeconds(timeToLiveSeconds);
        return this;
    }

    public CacheContainerBuilder<T> timeToLiveMinutes(long timeToLiveMinutes) {
        this.timeToLive = Duration.ofMinutes(timeToLiveMinutes);
        return this;
    }

    public CacheContainerBuilder<T> local(@NonNull LocalCache local) {
        this.local = local;
        return this;
    }

    public CacheContainerBuilder<T> localLive1s() {
        this.local = LocalCache.live1s;
        return this;
    }

    public CacheContainerBuilder<T> localLive2s() {
        this.local = LocalCache.live2s;
        return this;
    }

    public CacheContainerBuilder<T> localLive5s() {
        this.local = LocalCache.live5s;
        return this;
    }

    public CacheContainerBuilder<T> localLive1m() {
        this.local = LocalCache.live1m;
        return this;
    }

    public CacheContainerBuilder<T> localLive2m() {
        this.local = LocalCache.live2m;
        return this;
    }

    public CacheContainerBuilder<T> localLive5m() {
        this.local = LocalCache.live5m;
        return this;
    }

    public CacheContainerBuilder<T> localLive10m() {
        this.local = LocalCache.live10m;
        return this;
    }

    public CacheContainerBuilder<T> typing(boolean typing) {
        this.typing = typing;
        return this;
    }

    public CacheContainerBuilder<T> withTyping() {
        this.typing = true;
        return this;
    }

    public CacheContainerBuilder<T> value(@NonNull Function<String, T> value) {
        this.value = value;
        return this;
    }

    public CacheContainer<T> build() {
        if (this.timeToLive != null && this.timeToLive.getSeconds() < 1) {
            throw FrameworkException.of("[timeToLive]不能小于1秒");
        }
        if (this.timeToLive != null && this.local != null && this.timeToLive.toMillis() < this.local.getLocalCacheLiveMillis()) {
            throw FrameworkException.of("[timeToLive]不能小于[localCache]时间");
        }

        if (StrUtil.isBlank(this.keyTemplate)) {
            throw FrameworkException.of("[keyTemplate]不能为空");
        }
        return new CacheContainer<>(this.source, this.keyTemplate, this.timeToLive, this.local, this.clazz, this.typing, this.value);
    }


}
