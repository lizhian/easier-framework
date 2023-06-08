package tydic.framework.starter.logging;

import cn.hutool.core.lang.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.logging.appender.RedissonAppender;
import tydic.framework.starter.logging.converter.TraceIdConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        this.initProperties(environment, enableTydicLogging);
    }

    private void initProperties(ConfigurableEnvironment environment, EnableTydicLogging enableTydicLogging) {
        List<String> lines = StrUtil.lines("#日志配置文件\n" +
                        "logging.config=classpath:tydic/framework/starter/logging/xml/tydic-logback.xml\n" +
                        "\n" +
                        "#日志文件目录,仅在prod生效\n" +
                        "logging.file.path=./log\n" +
                        "\n" +
                        "#日志文件路径,仅在prod生效\n" +
                        "logging.file.name=${logging.file.path}/console.log\n" +
                        "\n" +
                        "#归档日志单个大小\n" +
                        "logging.logback.rollingpolicy.max-file-size=100MB\n" +
                        "\n" +
                        "#归档日志格式\n" +
                        "logging.logback.rollingpolicy.file-name-pattern=${logging.file.path}/history/%d{yyyy-MM-dd}/console.%i.log\n" +
                        "\n" +
                        "#归档日志保存时长\n" +
                        "logging.logback.rollingpolicy.max-history=30")
                .filter(StrUtil::isNotBlank)
                .filter(s -> !s.startsWith("#"))
                .filter(s -> s.contains("="))
                .collect(Collectors.toList());
        Map<String, Object> source = new HashMap<>();
        for (String line : lines) {
            String key = StrUtil.subBefore(line, "=", false);
            String value = StrUtil.subAfter(line, "=", false);
            if (environment.containsProperty(key)) {
                continue;
            }
            source.put(key, value);
            Console.log("写入默认配置【{}={}】", key, value);
        }
        MapPropertySource mapPropertySource = new MapPropertySource(this.getClass().getSimpleName(), source);
        environment.getPropertySources().addLast(mapPropertySource);
    }
}
