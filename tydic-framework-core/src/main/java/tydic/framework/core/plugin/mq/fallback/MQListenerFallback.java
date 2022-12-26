package tydic.framework.core.plugin.mq.fallback;

public interface MQListenerFallback {

    void onException(Object message, Exception e);
}
