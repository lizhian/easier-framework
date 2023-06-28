package easier.framework.core.plugin.innerRequest;


import cn.hutool.aop.ProxyUtil;
import easier.framework.core.util.InstanceUtil;
import easier.framework.core.util.SpringUtil;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 内部请求构建者
 */
public class InnerRequestBuilder implements InvocationHandler {

    public static <T extends InnerRequest> T build(Class<T> interfaces) {
        return InstanceUtil.in(InnerRequestBuilder.class)
                           .getInstance(interfaces, InnerRequestBuilder::newInstance);

    }

    private static <T> T newInstance(Class<T> clazz) {
        return ProxyUtil.newProxyInstance(new InnerRequestBuilder(), clazz);
    }


    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }
        InnerRequestBuilder.Invoker invoker = SpringUtil.getAndCache(InnerRequestBuilder.Invoker.class);
        if (invoker != null) {
            return invoker.invoke(this, method, args);
        }
        throw InnerRequestException.of("invoker为空,无法执行代理方法:{}", method);
    }

    public interface Invoker {
        Object invoke(Object proxy, Method method, Object[] args);
    }
}
