package easier.framework.core.plugin.codec;

import easier.framework.core.util.HessianUtil;

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
