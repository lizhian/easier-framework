package easier.framework.core.plugin.cache;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.annotation.AnnotationUtil;
import easier.framework.core.aop.DefaultMethodInvocationHandler;
import easier.framework.core.plugin.CacheTest;
import easier.framework.core.plugin.cache.annotation.EasierCache;
import easier.framework.core.plugin.cache.container.CacheContainer;
import easier.framework.core.plugin.cache.container.CacheContainer2;
import easier.framework.core.plugin.cache.interfaces.CacheInterface;
import easier.framework.core.plugin.cache.interfaces.CacheInterface2;
import easier.framework.core.plugin.exception.biz.FrameworkException;

public class Caches implements CacheInterface<Object> {
    public static void main(String[] args) {
        CacheTest caches = Caches.ofInterface(CacheTest.class);
        caches.get("1");
    }

    public static <T> CacheContainer.Builder<T> ofContainer() {
        return new CacheContainer.Builder<>();
    }

    public static <T> CacheContainer.Builder<T> ofContainer(Class<T> clazz) {
        return Caches.ofContainer();
    }

    public static <T> CacheContainer2.Builder<T> ofContainer2() {
        return new CacheContainer2.Builder<>();
    }

    public static <T> CacheContainer2.Builder<T> ofContainer2(Class<T> clazz) {
        return Caches.ofContainer2();
    }


    public static <I extends CacheInterface<T>, T> I ofInterface(Class<I> clazz) {
        EasierCache easierCache = AnnotationUtil.getAnnotation(clazz, EasierCache.class);
        if (easierCache == null) {
            throw FrameworkException.of("[{}]未找到[@EasierCache]", clazz.getSimpleName());
        }
        return ProxyUtil.newProxyInstance(new DefaultMethodInvocationHandler(), clazz);
    }

    public static <I extends CacheInterface2<T>, T> I ofInterface2(Class<I> clazz) {
        EasierCache easierCache = AnnotationUtil.getAnnotation(clazz, EasierCache.class);
        if (easierCache == null) {
            throw FrameworkException.of("[{}]未找到[@EasierCache]", clazz.getSimpleName());
        }
        return ProxyUtil.newProxyInstance(new DefaultMethodInvocationHandler(), clazz);
    }


}
