package easier.framework.starter.cache.message;

import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.plugin.message.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.ByteArrayCodec;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * redis主题消息容器
 *
 * @author lizhian
 * @date 2023年11月03日
 */
@Slf4j
public class RedisTopicMessageContainer<T> implements MessageContainer<T> {
    private final MessageProducer<T> producer;
    private final MessageConsumer<T> consumer;
    @Getter
    private final List<MessageConsumer<T>> consumers = new ArrayList<>();
    private final Supplier<MessageConsumer<T>> newConsumer;


    public RedisTopicMessageContainer(RedissonClient client, String topicName, Codec<T> codec) {
        RTopic topic = client.getTopic(topicName, ByteArrayCodec.INSTANCE);
        this.producer = new RedisTopicMessageProducer<>(topic, codec);
        this.consumer = new RedisTopicMessageConsumer<>(topic, codec);
        this.consumers.add(this.consumer);
        this.newConsumer = () -> {
            RedisTopicMessageConsumer<T> consumer = new RedisTopicMessageConsumer<>(topic, codec);
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

    /*@Override
    public void setConcurrentRichListener(int concurrency, int poll, @NotNull Duration delay, @NotNull Consumer<Message<T>> listener) {
        // 获取线程池执行器
        ThreadPoolTaskExecutor executor = SpringUtil.getExecutor();
        // 创建信号量对象
        final Semaphore semaphore = new Semaphore(concurrency);
        // 设置富监听器
        this.asConsumer().setRichListener(poll, delay, message -> {
            // 创建信号量任务运行对象
            SemaphoreRunnable semaphoreRunnable = new SemaphoreRunnable(() -> {
                // 接收消息
                RedisMessageUtil.receiveMessage(message);
                // 处理消息
                listener.accept(message);
            }, semaphore);
            // 提交任务到线程池执行
            executor.submit(semaphoreRunnable);
        });
    }*/


    /**
     * redis主题消息制作人
     *
     * @author lizhian
     * @date 2023年11月03日
     */
    @RequiredArgsConstructor
    public static class RedisTopicMessageProducer<T> implements MessageProducer<T> {

        private final RTopic topic;
        private final Codec<T> codec;

        @Override
        public void send(T message, @NotNull Consumer<Map<String, String>> headerConsumer) {
            if (message == null) {
                return;
            }
            Map<String, String> header = RedisMessageUtil.newHeader();
            headerConsumer.accept(header);
            byte[] bytes = RedisMessageUtil.toBytes(message, header, this.codec);
            this.topic.publishAsync(bytes);
        }
    }

    /**
     * redis topic消息消费者
     *
     * @author lizhian
     * @date 2023年11月03日
     */
    @RequiredArgsConstructor
    public static class RedisTopicMessageConsumer<T> implements MessageConsumer<T> {
        private final RTopic topic;
        private final Codec<T> codec;
        private volatile boolean listening = false;


        @NotNull
        @Override
        public Message<T> richPoll() {
            throw FrameworkException.of("<redis-topic> 类型不支持 richPoll() 方法");
        }

        @NotNull
        @Override
        public List<Message<T>> richPoll(int size) {
            throw FrameworkException.of("<redis-topic> 类型不支持 richPoll(int size) 方法");
        }

        @Override
        public void setRichListener(int poll, @NotNull Duration delay, @NotNull Consumer<Message<T>> listener) {
            if (this.listening) {
                throw FrameworkException.of("不可重复设置监听方法");
            }
            this.topic.addListener(Object.class, (channel, bytes) -> {
                Message<T> message = RedisMessageUtil.toMessage((byte[]) bytes, this.codec);
                if (message.isNotEmpty()) {
                    RedisMessageUtil.receiveMessage(message);
                    listener.accept(message);
                }
            });
            this.listening = true;
        }

    }
}
