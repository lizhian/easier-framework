package easier.framework.core.plugin.message.builder;

import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.plugin.message.MessageContainer;
import easier.framework.core.plugin.message.enums.MessageChannel;

public interface MessageContainerFactory {


    <T> MessageContainer<T> build(String source, String name, MessageChannel channel, Codec<T> codec);
}
