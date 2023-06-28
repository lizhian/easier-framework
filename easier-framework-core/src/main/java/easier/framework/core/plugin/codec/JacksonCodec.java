package easier.framework.core.plugin.codec;

import easier.framework.core.util.JacksonUtil;
import lombok.SneakyThrows;

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
