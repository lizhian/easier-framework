package tydic.framework.starter.mq.failback;

import cn.hutool.core.util.ReflectUtil;
import tydic.framework.core.plugin.mq.fallback.MQListenerFallback;
import tydic.framework.core.util.InstanceUtil;
import tydic.framework.core.util.SpringUtil;

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
