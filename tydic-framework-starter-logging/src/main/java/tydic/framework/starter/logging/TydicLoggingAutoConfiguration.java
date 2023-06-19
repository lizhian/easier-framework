package tydic.framework.starter.logging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import tydic.framework.starter.logging.appender.RedissonAppender;
import tydic.framework.starter.logging.converter.TraceIdConverter;

@Configuration(proxyBeanMethods = false)
@Import(RedissonAppender.class)
public class TydicLoggingAutoConfiguration implements EnvironmentPostProcessor, Ordered {
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 100;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Class<?> applicationClass = application.getMainApplicationClass();
        EnableTydicLogging enableTydicLogging = AnnotatedElementUtils.findMergedAnnotation(applicationClass, EnableTydicLogging.class);
        if (enableTydicLogging == null) {
            return;
        }
        TraceIdConverter.showTraceId = enableTydicLogging.showTraceId();
    }
}
