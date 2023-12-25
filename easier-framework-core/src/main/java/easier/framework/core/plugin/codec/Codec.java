package easier.framework.core.plugin.codec;


/**
 * 编解码器
 *
 * @author lizhian
 * @date 2023年10月24日
 */
public interface Codec<T> {

    byte[] serialize(T value);

    T deserialize(byte[] bytes);
}
