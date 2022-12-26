package tydic.framework.core.util;


import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 实例工具类
 */
@UtilityClass
public class InstanceUtil {
    private final Map<Class<?>, LRUCache<Object, Object>> ALL_CACHE = new ConcurrentHashMap<>();


    /**
     * 缓存域
     */
    public InstanceCacheHolder in(Class<?> scope) {
        LRUCache<Object, Object> cache = ALL_CACHE.computeIfAbsent(scope, it -> CacheUtil.newLRUCache(1024));
        return new InstanceCacheHolder(cache);
    }


    @AllArgsConstructor
    public class InstanceCacheHolder {
        private LRUCache<Object, Object> instanceCache;

        /**
         * 从缓存获取实例
         */
        public <K, V> V getInstance(K key, Function<? super K, ? extends V> function) {
            return (V) this.instanceCache.get(key, () -> function.apply(key));
        }

        /**
         * 从缓存获取实例
         */
        public <K, V> V getInstance(K key, Supplier<? extends V> supplier) {
            return (V) this.instanceCache.get(key, supplier::get);
        }
    }
}
