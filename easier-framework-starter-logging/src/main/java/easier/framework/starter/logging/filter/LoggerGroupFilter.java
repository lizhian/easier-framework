package easier.framework.starter.logging.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import easier.framework.core.util.SpringUtil;
import lombok.Setter;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.logging.LoggerGroup;
import org.springframework.boot.logging.LoggerGroups;

import java.util.List;

public class LoggerGroupFilter extends AbstractMatcherFilter<ILoggingEvent> {

    private final LRUCache<String, FilterReply> cache = CacheUtil.newLRUCache(1000);
    @Setter
    public String group;

    @Override

    public FilterReply decide(ILoggingEvent event) {
        String loggerName = event.getLoggerName();
        return this.cache.get(loggerName, () -> this.decide(loggerName));
    }

    private FilterReply decide(String loggerName) {
        LoggerGroups bean = SpringUtil.getBean(LoggingApplicationListener.LOGGER_GROUPS_BEAN_NAME);
        LoggerGroup loggerGroup = bean.get(this.group);
        List<String> members = loggerGroup.getMembers();
        if (members.stream().anyMatch(loggerName::startsWith)) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
}
