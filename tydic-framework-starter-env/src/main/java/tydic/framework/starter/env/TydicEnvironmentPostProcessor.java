package tydic.framework.starter.env;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import tydic.framework.core.util.StrUtil;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TydicEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered, ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    public static final String STARTER_ENV_RESOURCE_LOCATION = "META-INF/starter.env";
    public static final String PROPERTY_SOURCE_NAME = "default-starter-env";

    private static List<DefaultEnv> loadedDefaultEnvs = new ArrayList<>();


    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 100;
    }

    @Override
    @SneakyThrows
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Enumeration<URL> resources = ClassLoaderUtil.getContextClassLoader()
                .getResources(STARTER_ENV_RESOURCE_LOCATION);
        loadedDefaultEnvs = CollUtil.newArrayList(resources)
                .stream()
                .flatMap(this::formatDefaultEnvs)
                .filter(defaultEnv -> !environment.containsProperty(defaultEnv.getKey()))
                .sorted(Comparator.comparing(DefaultEnv::getKey))
                .collect(Collectors.toList());
        Map<String, Object> source = new LinkedHashMap<>();
        for (DefaultEnv defaultEnv : loadedDefaultEnvs) {
            String key = defaultEnv.getKey();
            String value = defaultEnv.getValue();
            source.put(key, value);
        }
        environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, source));
    }


    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        for (DefaultEnv defaultEnv : loadedDefaultEnvs) {
            String comment = defaultEnv.getComment();
            if (StrUtil.isNotBlank(comment)) {
                log.info("【{}】"
                        , comment
                );
            }
            String key = defaultEnv.getKey();
            String value = defaultEnv.getValue();
            log.info("{}={}"
                    , key
                    , key.contains("password") ? StrUtil.hide(value, 3, value.length() - 3) : value
            );
        }
    }

    @SneakyThrows
    private Stream<DefaultEnv> formatDefaultEnvs(URL url) {
        List<DefaultEnv> result = new ArrayList<>();
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
                DefaultEnv defaultEnv = DefaultEnv.builder()
                        .comment(lastComment)
                        .key(key)
                        .value(value)
                        .build();
                result.add(defaultEnv);
                lastComment = "";
            }
        }
        return result.stream();
    }
}
