package easier.framework.core.plugin.message.enums;


import easier.framework.core.plugin.message.builder.IKafkaMessageContainerFactory;
import easier.framework.core.plugin.message.builder.IRabbitMessageContainerFactory;
import easier.framework.core.plugin.message.builder.IRedisMessageContainerFactory;
import easier.framework.core.plugin.message.builder.MessageContainerFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 消息提供程序
 *
 * @author lizhian
 * @date 2023年11月24日
 */
@RequiredArgsConstructor
@Getter
public enum MessageProvider {
    redis(IRedisMessageContainerFactory.class), kafka(IKafkaMessageContainerFactory.class), rabbit(IRabbitMessageContainerFactory.class);
    private final Class<? extends MessageContainerFactory> factoryClass;
}
