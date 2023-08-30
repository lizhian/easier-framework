package easier.framework.core.plugin.cache.container;

import java.time.Duration;
import java.util.List;

public interface CacheContainerHelper {
    boolean has(String source, String key);

    byte[] get(String source, String key);

    void update(String source, String key, byte[] value);

    void update(String source, String key, byte[] value, Duration timeToLive);

    void clean(String source, String key);

    long size(String source, String pattern);

    List<String> keys(String source, String pattern);

    List<byte[]> values(String source, String pattern);

    void cleanAll(String source, String pattern);

}
