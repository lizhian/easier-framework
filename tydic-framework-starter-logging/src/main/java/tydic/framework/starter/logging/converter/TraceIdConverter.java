package tydic.framework.starter.logging.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import tydic.framework.core.util.TraceIdUtil;

/**
 * 显示 traceId
 */
public class TraceIdConverter extends ClassicConverter {

    public volatile static boolean showTraceId = false;

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        if (TraceIdConverter.showTraceId) {
            return TraceIdUtil.getOrDefault("------------------");
        }
        return "";
    }
}
