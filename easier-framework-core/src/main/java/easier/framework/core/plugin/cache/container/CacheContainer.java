package easier.framework.core.plugin.cache.container;

import easier.framework.core.plugin.cache.enums.LocalCache;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.util.JacksonUtil;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import lombok.Data;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class CacheContainer<T> {
    /**
     * 缓存源
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
    private final LocalCache local;
    /**
     * value类型
     */
    private final Class<T> clazz;
    /**
     * 系列化记录value类型
     */
    private final boolean typing;
    /**
     * value获取函数
     */
    private final Function<String, T> value;

    private CacheContainerHelper helper() {
        CacheContainerHelper helper = SpringUtil.getAndCache(CacheContainerHelper.class);
        if (helper == null) {
            throw FrameworkException.of("未找到[{}]实现类", CacheContainerHelper.class.getSimpleName());
        }
        return helper;
    }

    private Object bytesToValue(byte[] bytes) {
        if (this.typing) {
            return JacksonUtil.toObject(bytes);
        }
        return JacksonUtil.toBean(bytes, this.clazz);
    }

    private byte[] valueToBytes(T value) {
        if (this.typing) {
            return JacksonUtil.toTypingBytes(value);
        }
        return JacksonUtil.toBytes(value);
    }

    /**
     * 判断是否存在
     *
     * @param key 参数
     * @return boolean
     */
    public boolean has(String key) {
        String realKey = StrUtil.format(this.keyTemplate, key);
        Object valueInLocal = this.local.get(key);
        if (valueInLocal != null) {
            return true;
        }
        return this.helper().has(this.source, realKey);
    }


    /**
     * 获取缓存值
     *
     * @param key 参数
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T get(String key) {
        String realKey = StrUtil.format(this.keyTemplate, key);
        Object valueInLocal = this.local.get(realKey);
        if (valueInLocal != null) {
            return (T) valueInLocal;
        }
        byte[] bytes = this.helper().get(this.source, realKey);
        if (bytes != null) {
            return (T) this.bytesToValue(bytes);
        }
        T realValue = this.value.apply(key);
        if (realValue == null) {
            return null;
        }
        this.local.update(realKey, realValue);
        bytes = this.valueToBytes(realValue);
        this.helper().update(this.source, realKey, bytes, this.timeToLive);
        return realValue;
    }

    /**
     * 更新缓存值
     *
     * @param param 参数
     * @param value 值
     */
    public void update(String key, T value) {
        this.update(key, value, this.timeToLive);
    }

    /**
     * 更新缓存值
     *
     * @param param      参数
     * @param value      值
     * @param timeToLive 活着时间
     */
    public void update(String key, T value, Duration timeToLive) {
        String realKey = StrUtil.format(this.keyTemplate, key);
        this.local.update(realKey, value);
        byte[] bytes = this.valueToBytes(value);
        this.helper().update(this.source, realKey, bytes, timeToLive);
    }

    /**
     * 清除缓存
     *
     * @param param 参数
     */
    public void clean(String key) {
        String realKey = StrUtil.format(this.keyTemplate, key);
        this.local.clean(realKey);
        this.helper().clean(this.source, realKey);
    }

    /**
     * 查看数量
     */
    public long size() {
        String pattern = StrUtil.format(this.keyTemplate, "*");
        return this.helper().size(this.source, pattern);
    }

    /**
     * 清除全部
     */
    public void cleanAll() {
        String pattern = StrUtil.format(this.keyTemplate, "*");
        this.local.cleanAll();
        this.helper().cleanAll(this.source, pattern);
    }

    public List<String> keys() {
        String pattern = StrUtil.format(this.keyTemplate, "*");
        return this.helper().keys(this.source, pattern);
    }

    @SuppressWarnings("unchecked")
    public List<T> values() {
        String pattern = StrUtil.format(this.keyTemplate, "*");
        return this.helper().values(this.source, pattern)
                .stream()
                .map(bytes -> (T) this.bytesToValue(bytes))
                .collect(Collectors.toList());
    }
}
