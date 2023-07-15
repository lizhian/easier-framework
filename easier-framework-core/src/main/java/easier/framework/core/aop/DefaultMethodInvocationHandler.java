package easier.framework.core.aop;

import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.util.DefaultMethodUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DefaultMethodInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }
        if (method.isDefault()) {
            return DefaultMethodUtil.invoke(proxy, method, args);
        }
        throw FrameworkException.of("此代理对象仅支持默认方法");
    }
}
