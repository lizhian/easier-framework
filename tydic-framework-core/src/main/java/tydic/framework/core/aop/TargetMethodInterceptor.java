package tydic.framework.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;

/**
 * 方法拦截器
 */
@Slf4j
public abstract class TargetMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object proxy = invocation.getThis();
        Class<?> targetClass = null;
        if (proxy != null) {
            targetClass = AopUtils.getTargetClass(proxy);
        }
        Method method = AopUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
        return this.invoke(invocation, proxy, targetClass, method);
    }

    public abstract Object invoke(MethodInvocation invocation, Object proxy, Class<?> targetClass, Method method) throws Throwable;
}
