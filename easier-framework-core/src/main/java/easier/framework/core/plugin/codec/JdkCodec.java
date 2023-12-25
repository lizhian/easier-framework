package easier.framework.core.plugin.codec;


import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;

/**
 * jdk编解码器
 *
 * @author lizhian
 * @date 2023年10月24日
 */
@RequiredArgsConstructor
public class JdkCodec<T> implements Codec<T> {
    private final Class<T> clazz;

    @Override
    public byte[] serialize(Object value) {
        if (value == null) {
            return new byte[0];
        }
        return ObjectUtil.serialize(value);
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return ObjectUtil.deserialize(bytes, this.clazz);
    }
}
