package easier.framework.core.plugin.mq.container;

import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.plugin.mq.enums.MessageChannelType;
import easier.framework.core.plugin.mq.enums.MessageServerType;
import lombok.Data;

@Data
public class BinaryMessageContainer extends MessageContainer<byte[]> {


    public BinaryMessageContainer(String source, String name, MessageChannelType messageChannelType, MessageServerType messageServerType, Class<byte[]> clazz, boolean autoType, Codec codec) {
        super(source, name, messageChannelType, messageServerType, clazz, autoType, codec);
    }
}
