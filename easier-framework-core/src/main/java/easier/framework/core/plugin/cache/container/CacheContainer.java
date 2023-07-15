package easier.framework.core.plugin.cache.container;

import easier.framework.core.plugin.cache.enums.LocalCache;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.util.function.Function;

@Data
public class CacheContainer<T> {
    /**
     * 来源
     */
    private final String source;
    /**
     * key模板
     */
    private final String keyTemplate;
    /**
     * 活着时间
     */
    private final Duration timeToLive;
    /**
     * 本地缓存
     */
    private final LocalCache localCache;
    /**
     * value获取函数
     */
    private final Function<String, T> valueFunction;

    public boolean has(String param) {
        CacheContainerHelper helper = SpringUtil.getBean(CacheContainerHelper.class);
        if (helper == null) {
            throw FrameworkException.of("未找到[{}]实现类", CacheContainerHelper.class.getSimpleName());
        }
        return helper.has(
                this.source,
                () -> StrUtil.format(this.keyTemplate, param),
                this.localCache
        );
    }

    public long size() {
        CacheContainerHelper helper = SpringUtil.getBean(CacheContainerHelper.class);
        if (helper == null) {
            throw FrameworkException.of("未找到[{}]实现类", CacheContainerHelper.class.getSimpleName());
        }
        return helper.size(
                this.source,
                () -> StrUtil.format(this.keyTemplate, "*")
        );
    }

    /**
     * 获取缓存值
     *
     * @param param 参数
     * @return {@link T}
     */
    public T get(String param) {
        CacheContainerHelper helper = SpringUtil.getBean(CacheContainerHelper.class);
        if (helper == null) {
            throw FrameworkException.of("未找到[{}]实现类", CacheContainerHelper.class.getSimpleName());
        }
        return helper.get(
                this.source,
                () -> StrUtil.format(this.keyTemplate, param),
                () -> this.valueFunction.apply(param),
                this.timeToLive,
                this.localCache
        );
    }

    /**
     * 更新缓存值
     *
     * @param param 参数
     * @param value 值
     */
    public void update(String param, T value) {
        this.update(param, value, this.timeToLive);
    }

    /**
     * 更新缓存值
     *
     * @param param      参数
     * @param value      值
     * @param timeToLive 活着时间
     */
    public void update(String param, T value, Duration timeToLive) {
        CacheContainerHelper helper = SpringUtil.getBean(CacheContainerHelper.class);
        if (helper == null) {
            throw FrameworkException.of("未找到[{}]实现类", CacheContainerHelper.class.getSimpleName());
        }
        helper.update(
                this.source,
                () -> StrUtil.format(this.keyTemplate, param),
                () -> value,
                timeToLive,
                this.localCache
        );
    }

    /**
     * 清除缓存
     *
     * @param param 参数
     */
    public void clean(String param) {
        CacheContainerHelper helper = SpringUtil.getBean(CacheContainerHelper.class);
        if (helper == null) {
            throw FrameworkException.of("未找到[{}]实现类", CacheContainerHelper.class.getSimpleName());
        }
        helper.clean(
                this.source,
                () -> StrUtil.format(this.keyTemplate, param),
                this.localCache
        );
    }

    /**
     * 清除全部
     */
    public void cleanAll() {
        CacheContainerHelper helper = SpringUtil.getBean(CacheContainerHelper.class);
        if (helper == null) {
            throw FrameworkException.of("未找到[{}]实现类", CacheContainerHelper.class.getSimpleName());
        }
        helper.cleanAll(
                this.source,
                () -> StrUtil.replace(this.keyTemplate, "{}", "*"),
                this.localCache
        );
    }


    public static class Builder<T> {
        private String source;
        private String keyTemplate;
        private Duration timeToLive;
        private LocalCache localCache;
        private Function<String, T> valueFunction;


        public Builder<T> source(@NonNull String source) {
            this.source = source;
            return this;
        }

        public Builder<T> keyTemplate(@NonNull String keyTemplate) {
            this.keyTemplate = keyTemplate;
            return this;
        }

        public Builder<T> keyPrefix(@NonNull String keyPrefix) {
            if (keyPrefix.endsWith(":")) {
                this.keyTemplate = keyPrefix + "{}";
            } else {
                this.keyTemplate = keyPrefix + ":{}";
            }
            return this;
        }

        public Builder<T> timeToLive(@NonNull Duration timeToLive) {
            this.timeToLive = timeToLive;
            return this;
        }

        public Builder<T> timeToLiveSeconds(long timeToLiveSeconds) {
            this.timeToLive = Duration.ofSeconds(timeToLiveSeconds);
            return this;
        }

        public Builder<T> timeToLiveMinutes(long timeToLiveMinutes) {
            this.timeToLive = Duration.ofMinutes(timeToLiveMinutes);
            return this;
        }

        public Builder<T> localCache(@NonNull LocalCache localCache) {
            this.localCache = localCache;
            return this;
        }

        public Builder<T> localCache1s() {
            this.localCache = LocalCache.live1s;
            return this;
        }

        public Builder<T> localCache2s() {
            this.localCache = LocalCache.live2s;
            return this;
        }

        public Builder<T> localCache5s() {
            this.localCache = LocalCache.live5s;
            return this;
        }

        public Builder<T> localCache1m() {
            this.localCache = LocalCache.live1m;
            return this;
        }

        public Builder<T> localCache2m() {
            this.localCache = LocalCache.live2m;
            return this;
        }

        public Builder<T> localCache5m() {
            this.localCache = LocalCache.live5m;
            return this;
        }

        public Builder<T> localCache10m() {
            this.localCache = LocalCache.live10m;
            return this;
        }

        public Builder<T> value(@NonNull Function<String, T> valueFunction) {
            this.valueFunction = valueFunction;
            return this;
        }

        public CacheContainer<T> build() {
            if (this.timeToLive != null && this.localCache != null && this.timeToLive.toMillis() < this.localCache.getLocalCacheLiveMillis()) {
                throw FrameworkException.of("[timeToLive]不能小于[localCache]时间");
            }
            if (this.timeToLive != null && this.timeToLive.getSeconds() < 1) {
                throw FrameworkException.of("[timeToLive]不能小于1秒");
            }
            if (StrUtil.isBlank(this.keyTemplate)) {
                throw FrameworkException.of("[keyTemplate]不能为空");
            }
            if (this.valueFunction == null) {
                this.valueFunction = param -> null;
            }
            if (this.localCache == null) {
                this.localCache = LocalCache.not;
            }
            return new CacheContainer<>(
                    this.source,
                    this.keyTemplate,
                    this.timeToLive,
                    this.localCache,
                    this.valueFunction
            );
        }


    }
}
