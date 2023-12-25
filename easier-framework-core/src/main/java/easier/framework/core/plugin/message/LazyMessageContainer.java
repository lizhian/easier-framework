package easier.framework.core.plugin.message;

import com.google.common.base.Supplier;
import lombok.RequiredArgsConstructor;

/**
 * 懒加载消息容器
 */

@RequiredArgsConstructor
public class LazyMessageContainer<T> implements MessageContainer<T> {
    private final Supplier<MessageContainer<T>> delegate;


    @Override
    public MessageProducer<T> asProducer() {
        return this.delegate.get().asProducer();
    }

    @Override
    public MessageConsumer<T> asConsumer() {
        return this.delegate.get().asConsumer();
    }

    @Override
    public MessageConsumer<T> newConsumer() {
        return this.delegate.get().newConsumer();
    }

    /*@Override
    public void setConcurrentRichListener(int concurrency, int poll, @NotNull Duration delay, @NotNull Consumer<Message<T>> listener) {
        this.delegate.get().setConcurrentRichListener(concurrency, poll, delay, listener);
    }*/
}
