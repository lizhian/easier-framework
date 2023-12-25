package easier.framework.core.plugin.cache.container;

import cn.hutool.core.lang.Pair;
import com.google.common.base.Supplier;
import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder(access = AccessLevel.PACKAGE)
public class CacheContainer<T> {
    private static final byte[] _null_value_bytes = StrUtil.utf8Bytes("this_an_null_value");
    /**
     * 缓存源
     */
    private final String source;
    /**
     * key模板
     */
    private final String keyTemplate;
    /**
     * 缓存时间
     */
    private final Duration timeToLive;
    /**
     * 是否在redis缓存null值
     */
    private final boolean nullable;
    /**
     * 本地缓存
     */
    private final LocalCache<T> local;
    /**
     * value获取函数
     */
    private final Function<String, T> mappingIfAbsent;
    /**
     * 编解码器
     */
    private final Codec<T> codec;
    /**
     * redis助手
     * <br/>
     * 懒加载
     */
    private final Supplier<CacheContainerHelper> helper = SpringUtil.lazyBean(CacheContainerHelper.class);


    /**
     * 查看数量
     */
    public long size() {
        String pattern = StrUtil.format(this.keyTemplate, "*");
        return this.helper.get().size(this.source, pattern);
    }

    /**
     * 获取所有键
     */
    public List<String> keys() {
        String pattern = StrUtil.format(this.keyTemplate, "*");
        return this.helper.get().keys(this.source, pattern);
    }

    /**
     * 获取所有值
     */
    public List<T> values() {
        String pattern = StrUtil.format(this.keyTemplate, "*");
        return this.helper.get()
                .values(this.source, pattern)
                .stream()
                .map(this.codec::deserialize)
                .collect(Collectors.toList());
    }

    /**
     * 判断是否存在
     *
     * @param key 参数
     * @return boolean
     */
    public boolean has(String key) {
        T valueInLocal = this.local.get(key);
        if (valueInLocal != null) {
            return true;
        }
        String redisKey = StrUtil.format(this.keyTemplate, key);
        return this.helper.get().has(this.source, redisKey);
    }


    /**
     * 获取缓存值
     *
     * @param key 参数
     * @return {@link T}
     */
    public T get(String key) {
        Pair<Boolean, T> pair = this._get(key);
        if (pair.getKey()) {
            return pair.getValue();
        }
        if (this.mappingIfAbsent == null) {
            return null;
        }
        CacheContainerHelper helper = this.helper.get();
        String redisKey = StrUtil.format(this.keyTemplate, key);
        String redisLockKey = StrUtil.format(this.keyTemplate, "_lock:" + key);
        String redisNullKey = StrUtil.format(this.keyTemplate, "_null:" + key);
        // 全局公平锁,执行更新操作
        return helper.lock(this.source, redisLockKey, () -> {
            Pair<Boolean, T> pairAgain = this._get(key);
            if (pairAgain.getKey()) {
                return pairAgain.getValue();
            }
            T mappingValue = this.mappingIfAbsent.apply(key);
            if (mappingValue == null) {
                if (this.nullable) {
                    helper.update(this.source, redisNullKey, _null_value_bytes, this.timeToLive);
                }
                helper.clean(this.source, redisKey);
                this.local.clean(key);
                return null;
            }
            byte[] valueBytes = this.codec.serialize(mappingValue);
            helper.update(this.source, redisKey, valueBytes, this.timeToLive);
            helper.clean(this.source, redisNullKey);
            this.local.update(key, mappingValue);
            return mappingValue;
        });

    }

    /**
     * 获取缓存对象
     *
     * @param key 键
     * @return {@link Pair}<{@link Boolean 是否命中}, {@link T 缓存对象}>
     */
    private Pair<Boolean, T> _get(String key) {
        T valueInLocal = this.local.get(key);
        if (valueInLocal != null) {
            return Pair.of(true, valueInLocal);
        }
        String redisKey = StrUtil.format(this.keyTemplate, key);
        String redisNullKey = StrUtil.format(this.keyTemplate, "_null:" + key);
        byte[] bytes = this.helper.get().get(this.source, redisKey);
        if (bytes != null) {
            T valueInRedis = this.codec.deserialize(bytes);
            this.local.update(key, valueInRedis);
            return Pair.of(true, valueInRedis);
        }
        if (this.helper.get().has(this.source, redisNullKey)) {
            return Pair.of(true, null);
        } else {
            return Pair.of(false, null);
        }
    }


    /**
     * 更新缓存值
     *
     * @param key   参数
     * @param value 值
     */
    public void update(String key, T value) {
        this.update(key, value, this.timeToLive);
    }

    /**
     * 更新缓存值
     *
     * @param key        参数
     * @param value      值
     * @param timeToLive 有效时间
     */
    public void update(String key, T value, Duration timeToLive) {
        CacheContainerHelper helper = this.helper.get();
        String redisKey = StrUtil.format(this.keyTemplate, key);
        String redisLockKey = StrUtil.format(this.keyTemplate, "_lock:" + key);
        String redisNullKey = StrUtil.format(this.keyTemplate, "_null:" + key);
        // 全局公平锁,执行更新操作
        helper.lock(this.source, redisLockKey, () -> {
            if (value == null) {
                if (this.nullable) {
                    helper.update(this.source, redisNullKey, _null_value_bytes, timeToLive);
                }
                helper.clean(this.source, redisKey);
                this.local.clean(key);
            }
            byte[] valueBytes = this.codec.serialize(value);
            helper.update(this.source, redisKey, valueBytes, timeToLive);
            helper.clean(this.source, redisNullKey);
            this.local.update(key, value);
        });
    }


    /**
     * 清除缓存
     */
    public void clean(String key) {
        CacheContainerHelper helper = this.helper.get();
        String redisKey = StrUtil.format(this.keyTemplate, key);
        String redisLockKey = StrUtil.format(this.keyTemplate, "_lock:" + key);
        String redisNullKey = StrUtil.format(this.keyTemplate, "_null:" + key);
        // 全局公平锁,执行清除操作
        helper.lock(this.source, redisLockKey, () -> {
            helper.clean(this.source, redisKey);
            helper.clean(this.source, redisNullKey);
            this.local.clean(key);
        });
    }


    /**
     * 清楚所有缓存
     */
    public synchronized void cleanAll() {
        String keyPattern = StrUtil.format(this.keyTemplate, "*");
        String nullKeyPattern = StrUtil.format(this.keyTemplate, "_null:*");
        CacheContainerHelper helper = this.helper.get();
        helper.cleanAll(this.source, keyPattern);
        helper.cleanAll(this.source, nullKeyPattern);
        this.local.cleanAll();
    }
}
