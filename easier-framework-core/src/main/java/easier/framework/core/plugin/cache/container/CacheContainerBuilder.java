package easier.framework.core.plugin.cache.container;

import easier.framework.core.plugin.codec.Codec;
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
     * 缓存时间
     */
    private Duration timeToLive;
    /**
     * null值是否可缓存
     */
    private boolean nullValueCacheable;
    /**
     * 本地缓存
     */
    private LocalCache<T> localCache;
    /**
     * 本地缓存生存时间
     */
    private Duration localCacheTimeToLive;
    /**
     * value获取函数
     */
    private Function<String, T> mappingIfAbsent;
    /**
     * 编解码器
     */
    private Codec<T> codec;

    /**
     * 缓存源
     */
    public CacheContainerBuilder<T> source(@NonNull String source) {
        this.source = source;
        return this;
    }

    /**
     * key模板
     */
    public CacheContainerBuilder<T> keyTemplate(@NonNull String keyTemplate) {
        this.keyTemplate = keyTemplate;
        return this;
    }

    /**
     * key前缀
     */
    public CacheContainerBuilder<T> keyPrefix(@NonNull String keyPrefix) {
        if (keyPrefix.endsWith(":")) {
            this.keyTemplate = keyPrefix + "{}";
        } else {
            this.keyTemplate = keyPrefix + ":{}";
        }
        return this;
    }


    /**
     * 缓存时间
     */
    public CacheContainerBuilder<T> timeToLive(@NonNull Duration timeToLive) {
        this.timeToLive = timeToLive;
        return this;
    }

    /**
     * 缓存时间 秒
     */
    public CacheContainerBuilder<T> timeToLiveSeconds(long timeToLiveSeconds) {
        this.timeToLive = Duration.ofSeconds(timeToLiveSeconds);
        return this;
    }

    /**
     * 缓存时间 分钟
     */
    public CacheContainerBuilder<T> timeToLiveMinutes(long timeToLiveMinutes) {
        this.timeToLive = Duration.ofMinutes(timeToLiveMinutes);
        return this;
    }

    /**
     * 缓存时间 永不过期
     */
    public CacheContainerBuilder<T> timeToLiveForever() {
        this.timeToLive = null;
        return this;
    }

    /**
     * 是否在redis缓存null值
     */
    public CacheContainerBuilder<T> nullValueCacheable(boolean nullValueCacheable) {
        this.nullValueCacheable = nullValueCacheable;
        return this;
    }

    /**
     * 禁用本地缓存
     */
    public CacheContainerBuilder<T> disableLocalCache() {
        this.localCache = new LocalCache<>();
        this.localCacheTimeToLive = null;
        return this;
    }


    /**
     * 本地缓存
     *
     * @param maximumSize 最大尺寸
     * @param timeToLive  缓存时间
     * @return {@link CacheContainerBuilder}<{@link T}>
     */
    public CacheContainerBuilder<T> enableLocalCache(int maximumSize, @NonNull Duration timeToLive) {
        this.localCache = new LocalCache<>(maximumSize, timeToLive);
        this.localCacheTimeToLive = timeToLive;
        return this;
    }

    /**
     * key转换value
     */
    public CacheContainerBuilder<T> mappingIfAbsent(Function<String, T> mappingIfAbsent) {
        this.mappingIfAbsent = mappingIfAbsent;
        return this;
    }

    /**
     * 编解码器
     */
    public CacheContainerBuilder<T> codec(@NonNull Codec<T> codec) {
        this.codec = codec;
        return this;
    }

    /**
     * 构建缓存容器
     */
    public CacheContainer<T> build() {
        if (StrUtil.isBlank(this.keyTemplate)) {
            throw FrameworkException.of("[keyTemplate]不能为空");
        }
        if (this.timeToLive != null) {
            if (this.timeToLive.getSeconds() < 1) {
                throw FrameworkException.of("[timeToLive]不能小于1秒");
            }
            if (this.localCache != null) {
                if (this.timeToLive.getSeconds() < this.localCacheTimeToLive.getSeconds()) {
                    throw FrameworkException.of("[timeToLive]不能小于[localCache]时间");
                }
            }
        }
        CacheContainer<T> build = CacheContainer.<T>builder()
                .source(this.source)
                .keyTemplate(this.keyTemplate)
                .timeToLive(this.timeToLive)
                .nullable(this.nullValueCacheable)
                .local(this.localCache)
                .build();
        return new CacheContainer<>(
                this.source,
                this.keyTemplate,
                this.timeToLive,
                this.nullValueCacheable,
                this.localCache,
                this.mappingIfAbsent,
                this.codec
        );
    }


}
