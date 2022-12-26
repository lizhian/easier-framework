package tydic.framework.core.plugin.codec;

import tydic.framework.core.util.HessianUtil;

public class HessianCodec implements Codec {

    @Override
    public byte[] serialize(Object object) {
        return HessianUtil.serialize(object);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return HessianUtil.deserialize(bytes);
    }
}
