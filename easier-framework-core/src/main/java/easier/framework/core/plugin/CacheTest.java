package easier.framework.core.plugin;

import easier.framework.core.plugin.cache.annotation.EasierCache;
import easier.framework.core.plugin.cache.interfaces.CacheInterface;

import java.util.Date;
import java.util.function.Function;

@EasierCache(source = "", prefix = "", timeToLiveSeconds = 0L)
public interface CacheTest extends CacheInterface<Object> {

    @Override
    default Function<String, Object> valueFunction() {
        return param -> new Date();
    }
}
