package easier.framework.core.plugin.message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 消息消费者
 */
public interface MessageConsumer<T> {
    /**
     * 获取一条数据
     */
    @Nullable
    default T poll() {
        return this.richPoll().getBody();
    }

    /**
     * 获取多条数据
     */
    @Nonnull
    default List<T> poll(int size) {
        return this.richPoll(size)
                .stream()
                .map(Message::getBody)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 获取一条数据
     */
    @Nonnull
    Message<T> richPoll();

    /**
     * 获取多条数据
     */
    @Nonnull
    List<Message<T>> richPoll(int size);

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    default void setListener(@Nonnull Consumer<T> listener) {
        this.setRichListener(message -> listener.accept(message.getBody()));
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    default void setListener(int poll, Duration delay, Consumer<T> listener) {
        this.setRichListener(poll, delay, message -> listener.accept(message.getBody()));
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    default void setRichListener(Consumer<Message<T>> listener) {
        this.setRichListener(MessageContainer.default_poll_size, MessageContainer.default_poll_delay, listener);
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    void setRichListener(int poll, @Nonnull Duration delay, @Nonnull Consumer<Message<T>> listener);
}
