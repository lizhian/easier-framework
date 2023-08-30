package easier.framework.core.plugin.cache.annotation;

import cn.hutool.core.annotation.AnnotationUtil;
import easier.framework.core.plugin.cache.Caches;
import easier.framework.core.plugin.cache.container.CacheContainer;
import easier.framework.core.plugin.cache.interfaces.CacheInterface;
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
                .local(easierCache.localCache())
                .value(cacheInterface.valueFunction())
                .build();
    }
}
