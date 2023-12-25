package easier.framework.core.plugin.mq.container;

import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.plugin.mq.enums.MessageChannelType;
import easier.framework.core.plugin.mq.enums.MessageServerType;

public class StringMessageContainer extends MessageContainer<String> {


    public StringMessageContainer(String source, String name, MessageChannelType messageChannelType, MessageServerType messageServerType, Class<String> clazz, boolean autoType, Codec codec) {
        super(source, name, messageChannelType, messageServerType, clazz, autoType, codec);
    }
}
