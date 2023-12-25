package easier.framework.starter.cache.message;

import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.plugin.message.MessageContainer;
import easier.framework.core.plugin.message.builder.IRedisMessageContainerFactory;
import easier.framework.core.plugin.message.enums.MessageChannel;
import easier.framework.core.plugin.message.enums.MessageProvider;
import easier.framework.starter.cache.redis.RedissonClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;


/**
 * redis消息容器工厂
 *
 * @author lizhian
 * @date 2023年11月03日
 */

@Slf4j
@RequiredArgsConstructor
public class RedisMessageContainerFactory implements IRedisMessageContainerFactory {
    private final RedissonClients redissonClients;

    @Override
    public <T> MessageContainer<T> build(String source, String name, MessageChannel channel, Codec<T> codec) {
        RedissonClient client = this.redissonClients.getClient(source);
        Integer version = this.redissonClients.geServerVersion(source);
        // todo 在5.0以下的redis版本,使用 queue 和 pubsub 实现queue和topic
        // todo 在5.0以上的redis版本,使用stream实现queue和topic
        log.info("创建消息容器: source=【{}】, name=【{}】, provider=【{}】, channel=【{}】, codec=【{}】", source, name, MessageProvider.redis, channel, codec.getClass().getSimpleName());
        if (MessageChannel.queue.equals(channel)) {
            return new RedisQueueMessageContainer<>(client, name, codec);
        }
        return new RedisTopicMessageContainer<>(client, name, codec);

    }
}
