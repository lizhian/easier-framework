package easier.framework.core.plugin.mq;

import cn.hutool.aop.ProxyUtil;
import easier.framework.core.util.InstanceUtil;
import easier.framework.core.util.SpringUtil;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 消息队列接口构建者
 */
public class MQBuilder implements InvocationHandler {

    /**
     * 根据接口构建动态代理实例
     */
    public static <T extends MQ> T build(Class<T> interfaces) {
        return InstanceUtil.in(MQBuilder.class)
                           .getInstance(interfaces, MQBuilder::newInstance);
    }

    private static <T> T newInstance(Class<T> clazz) {
        return ProxyUtil.newProxyInstance(new MQBuilder(), clazz);
    }

    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }
        MQBuilder.Invoker invoker = SpringUtil.getAndCache(MQBuilder.Invoker.class);
        if (invoker != null) {
            return invoker.invoke(proxy, method, args);
        }
        throw MQBuilderException.of("MQBuilder.Invoker为空,无法执行代理方法:{}", method);
    }

    public interface Invoker {
        Object invoke(Object proxy, Method method, Object[] args);
    }
}
