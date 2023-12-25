package easier.framework.core.plugin.message.fallback;

import easier.framework.core.plugin.codec.args.ArgsCodec;
import easier.framework.core.plugin.message.Message;
import easier.framework.core.plugin.message.enums.MessageChannel;
import easier.framework.core.plugin.message.enums.MessageProvider;

public interface MessageConsumerFallback {
    void onException(Message<Object[]> message, Throwable error, String source, String name, MessageChannel channel, MessageProvider provider, ArgsCodec codec);
}
