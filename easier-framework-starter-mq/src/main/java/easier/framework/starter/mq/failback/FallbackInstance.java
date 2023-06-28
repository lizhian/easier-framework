package easier.framework.starter.mq.failback;

import cn.hutool.core.util.ReflectUtil;
import easier.framework.core.plugin.mq.fallback.MQListenerFallback;
import easier.framework.core.util.InstanceUtil;
import easier.framework.core.util.SpringUtil;

public class FallbackInstance {
    public static <T extends MQListenerFallback> T get(Class<T> clazz) {
        return InstanceUtil.in(FallbackInstance.class)
                           .getInstance(clazz, FallbackInstance::newInstance);
    }

    private static <T extends MQListenerFallback> T newInstance(Class<T> clazz) {
        T bean = SpringUtil.getBeanDefaultNull(clazz);
        if (bean != null) {
            return bean;
        }
        return ReflectUtil.newInstance(clazz);
    }
}
