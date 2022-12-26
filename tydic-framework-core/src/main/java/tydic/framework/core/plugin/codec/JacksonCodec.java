package tydic.framework.core.plugin.codec;

import lombok.SneakyThrows;
import tydic.framework.core.util.JacksonUtil;

public class JacksonCodec implements Codec {

    @SneakyThrows
    @Override
    public byte[] serialize(Object object) {
        return JacksonUtil.toTypingBytes(object);
    }

    @SneakyThrows
    @Override
    public Object deserialize(byte[] bytes) {
        return JacksonUtil.toObject(bytes);
    }
}
