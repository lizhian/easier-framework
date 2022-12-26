package tydic.framework.core.plugin.codec;

import lombok.SneakyThrows;

public class JdkCodec implements Codec {

    @SneakyThrows
    @Override
    public byte[] serialize(Object object) {
        return null;
    }

    @SneakyThrows
    @Override
    public Object deserialize(byte[] bytes) {
        return null;
    }
}
