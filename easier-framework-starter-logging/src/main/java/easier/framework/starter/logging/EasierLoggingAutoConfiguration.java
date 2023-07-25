package easier.framework.starter.logging;

import easier.framework.starter.logging.appender.RedissonAppender;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

@Configuration(proxyBeanMethods = false)
@Import(RedissonAppender.class)
public class EasierLoggingAutoConfiguration implements Ordered {
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 100;
    }
}
