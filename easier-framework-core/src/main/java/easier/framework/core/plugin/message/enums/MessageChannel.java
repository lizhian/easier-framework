package easier.framework.core.plugin.message.enums;

public enum MessageChannel {
    /**
     * 一条消息只会被一个消费者消费
     */
    queue,
    /**
     * 一条消息会被所有消费者消费
     */
    topic
}
