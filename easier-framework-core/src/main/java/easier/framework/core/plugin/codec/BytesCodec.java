package easier.framework.core.plugin.codec;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 字节数组编解码器
 *
 * @author lizhian
 * @date 2023年10月24日
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BytesCodec implements Codec<byte[]> {
    public static final BytesCodec INSTANCE = new BytesCodec();

    @Override
    public byte[] serialize(byte[] value) {
        if (value == null) {
            return new byte[0];
        }
        return value;
    }

    @Override
    public byte[] deserialize(byte[] bytes) {
        if (bytes == null) {
            return new byte[0];
        }
        return bytes;
    }
}
