package easier.framework.test.listener;

import easier.framework.core.plugin.message.annotation.MessageConsumer;
import easier.framework.test.eo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestListener implements TestMessageBean {
    @Override
    @MessageConsumer(concurrency = 100)
    public void sendXXX(String a1, String a2) {
        log.info("收到 {} {}", a1, a2);
        // ThreadUtil.safeSleep(2000);
    }

    @Override
    @MessageConsumer(enable = false)
    public void send343(User a1, Map<String, Object> a2) {
        log.info("收到 {} {}", a1, a2);
    }
}
