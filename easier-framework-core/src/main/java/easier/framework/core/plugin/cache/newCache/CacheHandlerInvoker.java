package easier.framework.core.plugin.cache.newCache;

import java.time.Duration;

public interface CacheHandlerInvoker {
    long size(String source, String pattern);

    boolean has(String source, String key);

    Object get(String source, String key);

    boolean set(String source, String key, Object value);

    boolean set(String source, String key, Object value, Duration timeToLive);

    boolean delete(String source, String key);

    boolean deleteAll(String source, String pattern);
}
