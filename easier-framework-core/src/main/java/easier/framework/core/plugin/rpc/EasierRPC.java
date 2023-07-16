package easier.framework.core.plugin.rpc;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.annotation.AnnotationUtil;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.util.SpringUtil;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

public class EasierRPC {

    public static <T> T of(Class<T> clazz) {
        RpcClient rpcClient = AnnotationUtil.getAnnotation(clazz, RpcClient.class);
        if (rpcClient == null) {
            throw FrameworkException.of("[{}]未找到[@RpcClient]", clazz.getSimpleName());
        }
        ;
        return ProxyUtil.newProxyInstance(EasierRPC::invoke, clazz);
    }

    @SneakyThrows
    private static Object invoke(Object proxy, Method method, Object[] args) {

        EasierRPC.Client client = SpringUtil.getAndCache(EasierRPC.Client.class);
        if (client != null) {
            return client.invoke(proxy, method, args);
        }
        throw FrameworkException.of("未找到[EasierRPC.Client]实现类,无法执行代理方法:{}", method);
    }

    public interface Client {
        Object invoke(Object proxy, Method method, Object[] args);
    }
}
