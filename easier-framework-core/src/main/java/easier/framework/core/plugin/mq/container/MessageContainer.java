package easier.framework.core.plugin.mq.container;

import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.plugin.mq.enums.MessageChannelType;
import easier.framework.core.plugin.mq.enums.MessageServerType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class MessageContainer<T> {
    /**
     * 消息源
     */
    private final String source;
    /**
     * 消息队列名称
     */
    private final String name;
    /**
     * 消息通道类型
     */
    private final MessageChannelType messageChannelType;

    /**
     * 消息服务类型
     */
    private final MessageServerType messageServerType;

    /**
     * value 类型
     */
    private final Class<T> clazz;
    /**
     * value 自动类型转换
     */
    private final boolean autoType;

    /**
     * 编解码器
     */
    private final Codec codec;

    public boolean send(T message) {
        return true;
    }

    public boolean send(T message, Map<String, String> header) {
        return true;
    }
}
