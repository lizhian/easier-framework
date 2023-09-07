package easier.framework.starter.env;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.DesensitizedUtil;
import easier.framework.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 更简单环境后处理器
 *
 * @author lizhian
 * @date 2023年07月20日
 */
@Slf4j
public class EasierEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered, ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    public static final String STARTER_ENV_RESOURCE_LOCATION = "META-INF/starter.env";
    public static final String PROPERTY_SOURCE_NAME = "default-starter-env";

    private static List<DefaultProperty> loadedDefaultProperties = new ArrayList<>();


    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 100;
    }

    @Override
    @SneakyThrows
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Enumeration<URL> resources = ClassLoaderUtil.getContextClassLoader()
                .getResources(STARTER_ENV_RESOURCE_LOCATION);
        loadedDefaultProperties = CollUtil.newArrayList(resources)
                .stream()
                .flatMap(this::formatDefaultEnvs)
                .filter(defaultProperty -> !environment.containsProperty(defaultProperty.getKey()))
                .sorted(Comparator.comparing(DefaultProperty::getKey))
                .collect(Collectors.toList());
        Map<String, Object> source = new LinkedHashMap<>();
        if (!environment.containsProperty("spring.application.name")) {
            String applicationName = application.getMainApplicationClass().getSimpleName();
            source.put("spring.application.name", applicationName);
        }
        for (DefaultProperty defaultProperty : loadedDefaultProperties) {
            String key = defaultProperty.getKey();
            String value = defaultProperty.getValue();
            source.put(key, value);
        }
        environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, source));
    }


    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        for (DefaultProperty defaultProperty : loadedDefaultProperties) {
            String comment = defaultProperty.getComment();
            if (StrUtil.isNotBlank(comment)) {
                System.out.println("\n# " + comment);
            }
            String key = defaultProperty.getKey();
            String value = defaultProperty.getValue();
            if (key.contains("password") && !value.startsWith("${") && !value.startsWith("ENC(")) {
                value = DesensitizedUtil.password(value);
            }
            System.out.println(key + "=" + value);
        }
        for (DefaultProperty defaultProperty : loadedDefaultProperties) {
            String comment = defaultProperty.getComment();
            if (StrUtil.isNotBlank(comment)) {
                log.info("");
                log.info("# {}", comment);
            }
            String key = defaultProperty.getKey();
            String value = defaultProperty.getValue();
            if (key.contains("password")) {
                value = DesensitizedUtil.password(value);
            }
            log.info("{}={}", key, value);
        }
    }

    @SneakyThrows
    private Stream<DefaultProperty> formatDefaultEnvs(URL url) {
        List<DefaultProperty> result = new ArrayList<>();
        String content = IoUtil.readUtf8(url.openStream());
        List<String> lines = StrUtil.lines(content)
                .filter(StrUtil::isNotBlank)
                .map(StrUtil::trim)
                .collect(Collectors.toList());
        String lastComment = "";
        for (String line : lines) {
            if (line.startsWith("#")) {
                lastComment = StrUtil.subAfter(line, "#", false);
                continue;
            }
            if (line.contains("=")) {
                String key = StrUtil.subBefore(line, "=", false);
                String value = StrUtil.subAfter(line, "=", false);
                DefaultProperty defaultProperty = DefaultProperty.builder()
                        .comment(lastComment)
                        .key(key)
                        .value(value)
                        .build();
                result.add(defaultProperty);
                lastComment = "";
            }
        }
        return result.stream();
    }
}
