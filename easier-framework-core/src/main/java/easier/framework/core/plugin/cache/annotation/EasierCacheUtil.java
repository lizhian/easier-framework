package easier.framework.core.plugin.cache.annotation;

import cn.hutool.core.annotation.AnnotationUtil;
import easier.framework.core.plugin.cache.Caches;
import easier.framework.core.plugin.cache.container.CacheContainer;
import easier.framework.core.plugin.cache.container.CacheContainer2;
import easier.framework.core.plugin.cache.interfaces.CacheInterface;
import easier.framework.core.plugin.cache.interfaces.CacheInterface2;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import org.springframework.aop.framework.AopProxyUtils;

import java.util.Arrays;

public class EasierCacheUtil {
    public static EasierCache findEasierCache(Object proxy) {
        if (proxy == null) {
            return null;
        }
        Class<?>[] interfaces = AopProxyUtils.proxiedUserInterfaces(proxy);
        EasierCache easierCache = Arrays.stream(interfaces)
                .filter(it -> AnnotationUtil.hasAnnotation(it, EasierCache.class))
                .map(it -> AnnotationUtil.getAnnotation(it, EasierCache.class))
                .findAny()
                .orElse(null);
        if (easierCache == null) {
            throw FrameworkException.of("未找到[@EasierCache]");
        }
        return easierCache;
    }

    public static <T> CacheContainer<T> buildCacheContainer(EasierCache easierCache, CacheInterface<T> cacheInterface) {
        return Caches.<T>ofContainer()
                .source(easierCache.source())
                .keyPrefix(easierCache.prefix())
                .timeToLiveSeconds(easierCache.timeToLiveSeconds())
                .localCache(easierCache.localCache())
                .value(cacheInterface.valueFunction())
                .build();
    }

    public static <T> CacheContainer2<T> buildCacheContainer2(EasierCache easierCache, CacheInterface2<T> cacheInterface2) {
        return Caches.<T>ofContainer2()
                .source(easierCache.source())
                .keyPrefix(easierCache.prefix())
                .timeToLiveSeconds(easierCache.timeToLiveSeconds())
                .localCache(easierCache.localCache())
                .value(cacheInterface2.valueFunction())
                .build();
    }
}
