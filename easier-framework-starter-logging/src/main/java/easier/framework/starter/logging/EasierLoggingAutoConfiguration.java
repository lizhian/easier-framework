package easier.framework.starter.logging;

import easier.framework.starter.logging.appender.RedissonAppender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration(proxyBeanMethods = false)
@Import(RedissonAppender.class)
public class EasierLoggingAutoConfiguration implements EnvironmentPostProcessor, Ordered {
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 100;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    }
}
