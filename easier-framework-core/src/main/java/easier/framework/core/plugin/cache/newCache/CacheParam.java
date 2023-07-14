package easier.framework.core.plugin.cache.newCache;

import easier.framework.core.plugin.cache.enums.LocalCache;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.function.Supplier;

@Data
@Builder
public class CacheParam {
    private final String source;
    private final String keyPrefix;
    private final String key;
    private final Duration timeToLive;
    private final LocalCache localCache;
    private final Supplier<Object> value;
}
