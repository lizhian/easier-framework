package easier.framework.starter.cache.message;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.plugin.message.*;
import easier.framework.core.util.SpringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.ByteArrayCodec;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * redis队列消息容器
 *
 * @author lizhian
 * @date 2023年11月03日
 */
@Slf4j
public class RedisQueueMessageContainer<T> implements MessageContainer<T> {
    private final MessageProducer<T> producer;
    private final MessageConsumer<T> consumer;
    @Getter
    private final List<MessageConsumer<T>> consumers = new ArrayList<>();
    private final Supplier<MessageConsumer<T>> newConsumer;

    public RedisQueueMessageContainer(RedissonClient client, String queueName, Codec<T> codec) {
        RQueue<byte[]> queue = client.getQueue(queueName, ByteArrayCodec.INSTANCE);
        this.producer = new RedisQueueMessageProducer<>(queue, codec);
        this.consumer = new RedisQueueMessageConsumer<>(queue, codec);
        this.consumers.add(this.consumer);
        this.newConsumer = () -> {
            RedisQueueMessageConsumer<T> consumer = new RedisQueueMessageConsumer<>(queue, codec);
            this.consumers.add(consumer);
            return consumer;
        };
    }

    @Override
    public MessageProducer<T> asProducer() {
        return this.producer;
    }

    @Override
    public MessageConsumer<T> asConsumer() {
        return this.consumer;
    }

    @Override
    public MessageConsumer<T> newConsumer() {
        return this.newConsumer.get();
    }
    

    /**
     * redis队列消息生产者
     *
     * @author lizhian
     * @date 2023年11月03日
     */
    @RequiredArgsConstructor
    public static class RedisQueueMessageProducer<T> implements MessageProducer<T> {
        private final RQueue<byte[]> queue;
        private final Codec<T> codec;

        @Override
        public void send(T message, @NotNull Consumer<Map<String, String>> headerConsumer) {
            if (message == null) {
                return;
            }
            Map<String, String> header = RedisMessageUtil.newHeader();
            headerConsumer.accept(header);
            byte[] bytes = RedisMessageUtil.toBytes(message, header, this.codec);
            this.queue.offerAsync(bytes);
        }
    }

    /**
     * redis队列消息消费者
     *
     * @author lizhian
     * @date 2023年11月03日
     */
    @RequiredArgsConstructor
    public static class RedisQueueMessageConsumer<T> implements MessageConsumer<T> {
        private final RQueue<byte[]> queue;
        private final Codec<T> codec;
        private volatile boolean listening = false;

        @NotNull
        @Override
        public Message<T> richPoll() {
            byte[] bytes = this.queue.poll();
            return RedisMessageUtil.toMessage(bytes, this.codec);
        }

        @NotNull
        @Override
        public List<Message<T>> richPoll(int limit) {
            List<byte[]> list = this.queue.poll(limit);
            if (CollUtil.isEmpty(list)) {
                return new ArrayList<>();
            }
            return list.stream()
                    .map(bytes -> RedisMessageUtil.toMessage(bytes, this.codec))
                    .filter(Message::isNotEmpty)
                    .collect(Collectors.toList());
        }

        @Override
        public synchronized void setRichListener(int poll, @NotNull Duration delay, @NotNull Consumer<Message<T>> listener) {
            if (this.listening) {
                throw FrameworkException.of("不可重复设置监听方法");
            }
            Runnable runnable = () -> {
                while (true) {
                    List<byte[]> records = this.queue.poll(poll);
                    if (CollUtil.isEmpty(records)) {
                        break;
                    }
                    records.stream()
                            .map(bytes -> RedisMessageUtil.toMessage(bytes, this.codec))
                            .filter(Message::isNotEmpty)
                            .forEach(message -> {
                                RedisMessageUtil.receiveMessage(message);
                                listener.accept(message);
                            });
                }
            };
            SpringUtil.getScheduler().scheduleWithFixedDelay(runnable, delay);
            this.listening = true;
        }
    }
}
