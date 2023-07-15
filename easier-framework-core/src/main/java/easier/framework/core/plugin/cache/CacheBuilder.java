package easier.framework.core.plugin.cache;

import cn.hutool.aop.ProxyUtil;
import easier.framework.core.util.InstanceUtil;
import easier.framework.core.util.SpringUtil;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 缓存接口构建者
 */
@Deprecated

public class CacheBuilder implements InvocationHandler {

    /**
     * 根据接口构建动态代理实例
     */
    public static <T extends Cache> T build(Class<T> interfaces) {
        return InstanceUtil.in(CacheBuilder.class)
                .getInstance(interfaces, CacheBuilder::newInstance);
    }

    private static <T> T newInstance(Class<T> clazz) {
        return ProxyUtil.newProxyInstance(new CacheBuilder(), clazz);
    }

    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }
        CacheBuilder.Invoker invoker = SpringUtil.getAndCache(CacheBuilder.Invoker.class);
        if (invoker != null) {
            return invoker.invoke(proxy, method, args);
        }
        throw CacheBuilderException.of("CacheBuilder.Invoker为空,无法执行代理方法:{}", method);
    }

    public interface Invoker {
        Object invoke(Object proxy, Method method, Object[] args);
    }
}
