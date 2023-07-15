package easier.framework.core.plugin.cache.container;

import easier.framework.core.plugin.cache.enums.LocalCache;

import java.time.Duration;
import java.util.function.Supplier;

public interface CacheContainerHelper {
    boolean has(String source, String key);

    long size(String source, String pattern);

    Object get(String source, String key);

    void update(String source, String key, Object value);

    void update(String source, String key, Object value, Duration timeToLive);

    void clean(String source, String key);

    void cleanAll(String source, String pattern);

    default boolean has(String source, Supplier<String> keySupplier, LocalCache localCache) {
        String key = keySupplier.get();
        Object value = localCache.get(key);
        if (value != null) {
            return true;
        }
        return this.has(source, key);
    }

    default long size(String source, Supplier<String> patternSupplier) {
        String pattern = patternSupplier.get();
        return this.size(source, pattern);
    }

    @SuppressWarnings("unchecked")
    default <T> T get(String source, Supplier<String> keySupplier, Supplier<T> valueSupplier, Duration timeToLive, LocalCache localCache) {
        String key = keySupplier.get();
        Object value = localCache.get(key);
        if (value != null) {
            return (T) value;
        }
        value = this.get(source, key);
        if (value != null) {
            return (T) value;
        }
        value = valueSupplier.get();
        if (value == null) {
            return null;
        }
        localCache.update(key, value);
        if (timeToLive == null || timeToLive.toMillis() <= 0) {
            this.update(source, key, value);
        } else {
            this.update(source, key, value, timeToLive);
        }
        return (T) value;
    }

    default <T> void update(String source, Supplier<String> keySupplier, Supplier<T> valueSupplier, Duration timeToLive, LocalCache localCache) {
        String key = keySupplier.get();
        T value = valueSupplier.get();
        localCache.update(key, value);
        if (timeToLive == null || timeToLive.toMillis() <= 0) {
            this.update(source, key, value);
        } else {
            this.update(source, key, value, timeToLive);
        }
    }

    default void clean(String source, Supplier<String> keySupplier, LocalCache localCache) {
        String key = keySupplier.get();
        localCache.clean(key);
        this.clean(source, key);
    }

    default void cleanAll(String source, Supplier<String> patternSupplier, LocalCache localCache) {
        String pattern = patternSupplier.get();
        localCache.cleanAll();
        this.cleanAll(source, pattern);
    }
}
