package easier.framework.core.plugin.message;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * 消息容器
 */

public interface MessageContainer<T> {
    int default_poll_size = 100;
    Duration default_poll_delay = Duration.ofSeconds(1);

    MessageProducer<T> asProducer();

    MessageConsumer<T> asConsumer();

    MessageConsumer<T> newConsumer();

    default void send(T message) {
        this.asProducer().send(message);
    }

    default void setListener(@Nonnull Consumer<T> listener) {
        this.asConsumer().setListener(listener);
    }

    /*default void setConcurrentListener(int concurrency, @Nonnull Consumer<T> listener) {
        this.setConcurrentListener(concurrency, default_poll_size, default_poll_delay, listener);
    }

    default void setConcurrentRichListener(int concurrency, @Nonnull Consumer<Message<T>> listener) {
        this.setConcurrentRichListener(concurrency, default_poll_size, default_poll_delay, listener);
    }

    default void setConcurrentListener(int concurrency, int poll, @Nonnull Duration delay, @Nonnull Consumer<T> listener) {
        this.setConcurrentRichListener(concurrency, poll, delay, message -> listener.accept(message.getBody()));
    }

    void setConcurrentRichListener(int concurrency, int poll, @Nonnull Duration delay, @Nonnull Consumer<Message<T>> listener);*/


}
