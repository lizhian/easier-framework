package easier.framework.core.plugin.message.fallback;

import easier.framework.core.plugin.codec.args.ArgsCodec;
import easier.framework.core.plugin.message.Message;
import easier.framework.core.plugin.message.enums.MessageChannel;
import easier.framework.core.plugin.message.enums.MessageProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageConsumerLoggingFallback implements MessageConsumerFallback {
    @Override
    public void onException(Message<Object[]> message, Throwable error, String source, String name, MessageChannel channel, MessageProvider provider, ArgsCodec codec) {
        log.error("消费 {} 消息发生异常: {} ", name, error.getMessage(), error);
    }
}
