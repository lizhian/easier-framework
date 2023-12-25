package easier.framework.core.plugin.message;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FastByteBuffer;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import easier.framework.core.Easier;
import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.util.ClassUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * redis队列消息容器
 *
 * @author lizhian
 * @date 2023年11月03日
 */
@Slf4j
public class RedisMessageUtil {

    public static String getName(Method method) {
        return "Easier:Message:" + ClassUtil.shortClassName(method.getDeclaringClass()) + ":" + method.getName();
    }

    public static <T> byte[] toBytes(T message, Map<String, String> header, Codec<T> codec) {
        byte[] headerBytes = Easier.Json.toJsonBytes(header);
        byte[] bodyBytes = codec.serialize(message);
        FastByteBuffer buffer = new FastByteBuffer(4 + headerBytes.length + bodyBytes.length);
        buffer.append(NumberUtil.toBytes(headerBytes.length))
                .append(headerBytes)
                .append(bodyBytes);
        return buffer.toArray();
    }

    @SneakyThrows
    @Nonnull
    public static <T> Message<T> toMessage(byte[] bytes, Codec<T> codec) {
        if (ArrayUtil.isEmpty(bytes)) {
            return new Message<>(new LinkedHashMap<>(), null);
        }
        int headerLength = NumberUtil.toInt(ArrayUtil.sub(bytes, 0, 4));
        byte[] headerBytes = ArrayUtil.sub(bytes, 4, 4 + headerLength);
        byte[] bodyBytes = ArrayUtil.sub(bytes, 4 + headerLength, bytes.length + 1);
        Map<String, String> header = Easier.Json.toMap(headerBytes, String.class, String.class);
        T body = codec.deserialize(bodyBytes);
        return new Message<>(header, body);
    }

    public static Map<String, String> newHeader() {
        Map<String, String> header = new LinkedHashMap<>();
        header.put(Easier.TraceId.x_trace_id, Easier.TraceId.get());
        header.put(Message.x_send_time, DateTime.now().toString());
        return header;
    }


    public static <T> void receiveMessage(Message<T> message) {
        String trackId = message.getTrackId();
        Easier.TraceId.reset(trackId);
    }
}
