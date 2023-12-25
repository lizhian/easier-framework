package easier.framework.core.plugin.message.proxy;

import cn.hutool.core.util.ReflectUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import easier.framework.core.Easier;
import easier.framework.core.plugin.codec.args.ArgsCodec;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.plugin.message.MessageBean;
import easier.framework.core.plugin.message.MessageContainer;
import easier.framework.core.plugin.message.RedisMessageUtil;
import easier.framework.core.plugin.message.annotation.MessageProducer;
import easier.framework.core.plugin.message.enums.MessageChannel;
import easier.framework.core.plugin.message.enums.MessageProvider;
import easier.framework.core.util.AnnotationUtil;
import easier.framework.core.util.DefaultMethodUtil;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class MessageProdukerInvocationHandler implements InvocationHandler {
    private final Class<?> proxyInterface;
    private final Cache<Method, MessageContainer<Object[]>> _CACHE = Caffeine.newBuilder().build();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 判断是否是 hashCode 方法
        if (ReflectUtil.isHashCodeMethod(method)) {
            // 返回 hashCode
            return this.hashCode();
        }
        // 判断是否是 equals 方法
        if (ReflectUtil.isEqualsMethod(method)) {
            // 判断参数是否与当前对象相等
            return this.equals(args[0]);
        }
        // 判断是否是 toString 方法
        if (ReflectUtil.isToStringMethod(method)) {
            // 返回对象的类名加上代理接口的名称
            return this.getClass().getSimpleName() + " of " + this.proxyInterface.getName();
        }
        // 判断是否是默认方法
        if (method.isDefault()) {
            // 调用默认方法处理
            return DefaultMethodUtil.invoke(proxy, method, args);
        }
        // 从缓存中获取消息容器
        MessageContainer<Object[]> container = this._CACHE.get(method, this::newMessageContainer);
        // 如果容器为空
        if (container == null) {
            // 抛出异常，显示未找到配置信息
            throw FrameworkException.of("{} 未找到 @{} 配置", method.getName(), MessageContainer.class.getSimpleName());
        }
        // 发送消息
        container.send(args);
        // 如果方法的返回类型是 Boolean 或 boolean
        if (method.getReturnType().equals(Boolean.class) || method.getReturnType().equals(boolean.class)) {
            // 返回 true
            return true;
        }
        // 返回 null
        return null;
    }


    /**
     * 创建一个消息容器
     *
     * @param method 方法
     * @return 消息容器
     */
    @Nonnull
    private MessageContainer<Object[]> newMessageContainer(Method method) {
        MessageProducer annotation = AnnotationUtil.getAnnotation(method, MessageProducer.class);
        MessageProducer annotation1 = AnnotationUtil.getAnnotation(method.getDeclaringClass(), MessageProducer.class);
        MessageProducer annotation2 = AnnotationUtil.getAnnotation(MessageBean.class, MessageProducer.class);
        MessageProducer[] messageProducers = new MessageProducer[]{
                annotation,
                annotation1,
                annotation2
        };
        String source = AnnotationUtil.getFirstAvailableValue(MessageProducer::source, messageProducers);
        String name = AnnotationUtil.getFirstAvailableValue(RedisMessageUtil.getName(method), MessageProducer::source, messageProducers);
        MessageChannel channel = AnnotationUtil.getFirstAvailableValue(MessageProducer::channel, messageProducers);
        MessageProvider provider = AnnotationUtil.getFirstAvailableValue(MessageProducer::provider, messageProducers);
        Class<? extends ArgsCodec> codecClass = AnnotationUtil.getFirstAvailableValue(MessageProducer::codec, messageProducers);
        ArgsCodec codec = ReflectUtil.newInstanceIfPossible(codecClass);
        return Easier.Message.newMessage(Object[].class)
                .source(source)
                .name(name)
                .use(provider)
                .as(channel)
                .codec(codec)
                .build();
    }
}
