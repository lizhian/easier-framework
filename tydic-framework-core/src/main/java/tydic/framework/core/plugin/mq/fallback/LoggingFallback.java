package tydic.framework.core.plugin.mq.fallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingFallback implements MQListenerFallback {


    @Override
    public void onException(Object message, Exception exception) {
        log.error("消费消息【{}】发生异常【{}】", message, exception.getMessage(), exception);
    }
}
