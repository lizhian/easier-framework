package easier.framework.starter.cache.redis;

import cn.hutool.core.io.IoUtil;
import easier.framework.core.Easier;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

public class RedissonJacksonCodec extends BaseCodec {

    private final ClassLoader classLoader;

    public RedissonJacksonCodec() {
        this(null);
    }

    public RedissonJacksonCodec(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public RedissonJacksonCodec(ClassLoader classLoader, RedissonJacksonCodec codec) {
        this(classLoader);
    }


    @Override
    public ClassLoader getClassLoader() {
        if (this.classLoader != null) {
            return this.classLoader;
        }
        return super.getClassLoader();
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return (buf, state) -> {
            byte[] bytes = IoUtil.readBytes(new ByteBufInputStream(buf));
            return Easier.JsonTyped.toObject(bytes);
        };
    }

    @Override
    public Encoder getValueEncoder() {
        return in -> {
            byte[] bytes = Easier.JsonTyped.toJsonBytes(in);
            return ByteBufAllocator.DEFAULT.buffer().writeBytes(bytes);
        };
    }
}
