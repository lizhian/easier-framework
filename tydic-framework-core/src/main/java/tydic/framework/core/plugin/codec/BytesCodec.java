package tydic.framework.core.plugin.codec;

public class BytesCodec implements Codec {

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            return new byte[0];
        }
        return (byte[]) obj;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return bytes;
    }
}
