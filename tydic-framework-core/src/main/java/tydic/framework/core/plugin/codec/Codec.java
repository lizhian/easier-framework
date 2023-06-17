package tydic.framework.core.plugin.codec;

public interface Codec {
    byte[] serialize(Object object);

    Object deserialize(byte[] bytes);
}
