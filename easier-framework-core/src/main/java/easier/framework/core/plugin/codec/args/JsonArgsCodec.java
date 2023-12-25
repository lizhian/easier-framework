package easier.framework.core.plugin.codec.args;


import easier.framework.core.Easier;

/**
 * json编解码器
 *
 * @author lizhian
 * @date 2023年10月24日
 */
public class JsonArgsCodec implements ArgsCodec {
    @Override
    public byte[] serialize(Object[] value) {
        return Easier.JsonTyped.toJsonBytes(value);
    }

    @Override
    public Object[] deserialize(byte[] bytes) {
        return Easier.JsonTyped.toObject(bytes);
    }
}
