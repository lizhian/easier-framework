package easier.framework.starter.logging.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import easier.framework.core.util.TraceIdUtil;

public class NormalTraceIdFilter extends AbstractMatcherFilter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (TraceIdUtil.isDisable()) {
            return FilterReply.DENY;
        }
        return FilterReply.ACCEPT;
    }
}
