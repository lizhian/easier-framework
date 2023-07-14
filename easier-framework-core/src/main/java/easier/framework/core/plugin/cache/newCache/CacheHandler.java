package easier.framework.core.plugin.cache.newCache;

import easier.framework.core.plugin.cache.enums.LocalCache;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.function.Function;

@Data
@Builder
public class CacheHandler<T> {
    private final String source;
    private final String keyTemplate;
    private final Duration timeToLive;
    private final LocalCache localCache;
    private final Function<String, T> function;


    @SuppressWarnings("unchecked")
    public T get(String param) {
        String key = StrUtil.format(this.keyTemplate, param);
        Object value = this.localCache.get(key);
        if (value != null) {
            return (T) value;
        }
        CacheHandlerInvoker invoker = SpringUtil.getBean(CacheHandlerInvoker.class);
        if (invoker == null) {
            throw FrameworkException.of("未找到[{}]实现类", CacheHandlerInvoker.class.getSimpleName());
        }
        value = invoker.get(this.source, key);
        if (value != null) {
            return (T) value;
        }
        value = this.function.apply(param);
        if (value == null) {
            return null;
        }

        //        return invoker.get(this.source, this.keyPrefix, key, this.localCache, () -> this.function.apply(key), this.timeToLive);
        return null;
    }

    public boolean set(String key, T value) {
        return true;
    }

    public boolean set(String key, T value, Duration timeToLive) {
        return true;
    }

    public boolean delete(String key) {
        return true;
    }

    public boolean deleteAll() {
        return true;
    }
}
