package easier.framework.test.listener;

import easier.framework.core.plugin.message.MessageBean;
import easier.framework.core.plugin.message.annotation.MessageProducer;
import easier.framework.core.plugin.message.enums.MessageChannel;
import easier.framework.test.eo.User;

import java.util.Map;

@MessageProducer(channel = MessageChannel.topic)
public interface TestMessageBean extends MessageBean {

    void sendXXX(String a1, String a2);

    void send343(User a1, Map<String, Object> a2);

    default void sendXXX2(String a1, String a2) {
        this.sendXXX(a1, a2);
    }
}
