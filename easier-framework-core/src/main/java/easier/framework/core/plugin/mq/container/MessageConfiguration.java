package easier.framework.core.plugin.mq.container;

import easier.framework.core.plugin.mq.enums.MessageChannelType;
import easier.framework.core.plugin.mq.enums.MessageServerType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageConfiguration {
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
     * 消费者配置
     */
    private final Consumer consumer;

    @Data
    public static class Consumer {

    }
}
