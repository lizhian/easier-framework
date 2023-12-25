package easier.framework.starter.logging.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import easier.framework.core.Easier;

/**
 * 显示 traceId
 */
public class TraceIdConverter extends ClassicConverter {


    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        return Easier.TraceId.getOr("-------------------");
    }
}
