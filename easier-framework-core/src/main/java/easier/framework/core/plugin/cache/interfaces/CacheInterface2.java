package easier.framework.core.plugin.cache.interfaces;

import easier.framework.core.plugin.cache.annotation.EasierCache;
import easier.framework.core.plugin.cache.annotation.EasierCacheUtil;
import easier.framework.core.plugin.cache.container.CacheContainer2;
import easier.framework.core.plugin.exception.biz.FrameworkException;

import java.time.Duration;
import java.util.function.BiFunction;

/**
 * 缓存接口标记
 */
public interface CacheInterface2<T> {


    /**
     * 请在子类或子接口重写此方法
     * <p>
     * 未命中缓存的时候获取实时缓存值
     *
     * @return {@link BiFunction}<{@link String}, {@link String}, {@link T}>
     */
    default BiFunction<String, String, T> valueFunction() {
        throw FrameworkException.of("请在子类或子接口重写此方法");

    }

    default boolean has(String param1, String param2) {
        return this.asCacheContainer2().has(param1, param2);
    }


    default long size() {
        return this.asCacheContainer2().size();
    }

    /**
     * 获取缓存值
     *
     * @param param1 参数1
     * @param param2 参数2
     * @return {@link T}
     */
    default T get(String param1, String param2) {
        return this.asCacheContainer2().get(param1, param2);

    }

    /**
     * 更新缓存值
     *
     * @param value  值
     * @param param1 参数1
     * @param param2 参数2
     */
    default void update(String param1, String param2, T value) {
        this.asCacheContainer2().update(param1, param2, value);
    }

    /**
     * 更新缓存值
     *
     * @param value      值
     * @param timeToLive 活着时间
     * @param param1     参数1
     * @param param2     参数2
     */
    default void update(String param1, String param2, T value, Duration timeToLive) {
        this.asCacheContainer2().update(param1, param2, value, timeToLive);

    }

    /**
     * 清除缓存
     *
     * @param param1 参数1
     * @param param2 参数2
     */
    default void clean(String param1, String param2) {
        this.asCacheContainer2().clean(param1, param2);

    }

    /**
     * 清除全部
     */
    default void cleanAll() {
        this.asCacheContainer2().cleanAll();
    }


    default CacheContainer2<T> asCacheContainer2() {
        EasierCache easierCache = EasierCacheUtil.findEasierCache(this);
        return EasierCacheUtil.buildCacheContainer2(easierCache, this);
    }


}
