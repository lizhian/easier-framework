package tydic.framework.core.plugin.codec;

import cn.hutool.core.util.ReflectUtil;
import tydic.framework.core.plugin.mq.fallback.MQListenerFallback;
import tydic.framework.core.util.InstanceUtil;
import tydic.framework.core.util.SpringUtil;

public interface Codec {

    static <T extends MQListenerFallback> T getInstances(Class<T> clazz) {
        return InstanceUtil.in(Codec.class)
                           .getInstance(clazz, Codec::newInstance);
    }

    private static <T extends MQListenerFallback> T newInstance(Class<T> clazz) {
        T bean = SpringUtil.getBeanDefaultNull(clazz);
        if (bean != null) {
            return bean;
        }
        return ReflectUtil.newInstance(clazz);
    }

    byte[] serialize(Object object);

    Object deserialize(byte[] bytes);
}
