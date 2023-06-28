package easier.framework.core.plugin.codec;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;

public class JdkCodec implements Codec {

    @SneakyThrows
    @Override
    public byte[] serialize(Object object) {
        return ObjectUtil.serialize(object);
    }

    @SneakyThrows
    @Override
    public Object deserialize(byte[] bytes) {
        return IoUtil.readObj(new ByteArrayInputStream(bytes));
    }
}
