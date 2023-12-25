package easier.framework.core.plugin.message;

import cn.hutool.core.collection.CollUtil;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 消息生产者
 */
public interface MessageProducer<T> {
    default void send(T message) {
        this.send(message, header -> {
        });
    }

    void send(T message, @Nonnull Consumer<Map<String, String>> headerConsumer);

    default void send(Collection<T> messages) {
        if (CollUtil.isEmpty(messages)) {
            return;
        }
        messages.forEach(this::send);
    }


    default void send(Collection<T> messages, Consumer<Map<String, String>> headerConsumer) {
        if (CollUtil.isEmpty(messages)) {
            return;
        }
        messages.forEach(message -> this.send(message, headerConsumer));
    }
}
