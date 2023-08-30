package easier.framework.core.plugin.cache;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.annotation.AnnotationUtil;
import easier.framework.core.aop.DefaultMethodInvocationHandler;
import easier.framework.core.plugin.CacheTest;
import easier.framework.core.plugin.cache.annotation.EasierCache;
import easier.framework.core.plugin.cache.container.CacheContainerBuilder;
import easier.framework.core.plugin.cache.interfaces.CacheInterface;
import easier.framework.core.plugin.exception.biz.FrameworkException;

public class Caches implements CacheInterface<Object> {
    public static void main(String[] args) {
        CacheTest caches = Caches.ofInterface(CacheTest.class);
        caches.get("1");
    }


    public static <T> CacheContainerBuilder<T> ofContainer() {
        return new CacheContainerBuilder<>();
    }

    public static <T> CacheContainerBuilder<T> ofContainer(Class<T> clazz) {
        return new CacheContainerBuilder<>(clazz);
    }


    public static <I extends CacheInterface<T>, T> I ofInterface(Class<I> clazz) {
        EasierCache easierCache = AnnotationUtil.getAnnotation(clazz, EasierCache.class);
        if (easierCache == null) {
            throw FrameworkException.of("[{}]未找到[@EasierCache]", clazz.getSimpleName());
        }
        return ProxyUtil.newProxyInstance(new DefaultMethodInvocationHandler(), clazz);
    }
}
