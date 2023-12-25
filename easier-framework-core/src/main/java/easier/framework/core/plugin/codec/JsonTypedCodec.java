package easier.framework.core.plugin.codec;


import easier.framework.core.Easier;
import lombok.Data;

/**
 * json编解码器
 *
 * @author lizhian
 * @date 2023年10月24日
 */
@Data
public class JsonTypedCodec<T> implements Codec<T> {
    @Override
    public byte[] serialize(T value) {
        return Easier.JsonTyped.toJsonBytes(value);
    }

    @Override
    public T deserialize(byte[] bytes) {
        return Easier.JsonTyped.toObject(bytes);
    }
}
