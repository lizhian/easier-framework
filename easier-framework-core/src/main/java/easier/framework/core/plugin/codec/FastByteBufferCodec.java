package easier.framework.core.plugin.codec;


import cn.hutool.core.io.FastByteBuffer;

/**
 * 快速字节缓冲编解码器
 *
 * @author lizhian
 * @date 2023年11月21日
 */
public class FastByteBufferCodec implements Codec<FastByteBuffer> {


    @Override
    public byte[] serialize(FastByteBuffer value) {
        return value.toArray();
    }

    @Override
    public FastByteBuffer deserialize(byte[] bytes) {
        FastByteBuffer fastByteBuffer = new FastByteBuffer(bytes.length);
        fastByteBuffer.append(bytes);
        return fastByteBuffer;
    }


}
