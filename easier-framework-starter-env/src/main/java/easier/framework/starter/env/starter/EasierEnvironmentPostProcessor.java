package easier.framework.starter.env.starter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.DesensitizedUtil;
import easier.framework.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
 * 注入starter.env配置
 *
 * @author lizhian
 * @date 2023年07月20日
 */
@Slf4j
public class EasierEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered, ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    public static final String STARTER_ENV_RESOURCE_LOCATION = "META-INF/easier.env";
    public static final String PROPERTY_SOURCE_NAME = "default-starter-env";

    private static List<DefaultProperty> loadedDefaultProperties = new ArrayList<>();


    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 100;
    }

    /**
     * 在环境后处理配置环境时调用。
     *
     * @param environment 可配置环境
     * @param application {@link SpringApplication}
     */
    @Override
    @SneakyThrows
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 获取资源
        Enumeration<URL> resources = ClassLoaderUtil.getContextClassLoader()
                .getResources(STARTER_ENV_RESOURCE_LOCATION);
        // 获取加载的默认属性列表
        loadedDefaultProperties = CollUtil.newArrayList(resources)
                .stream()
                .flatMap(this::formatDefaultEnvs)
                .filter(defaultProperty -> !environment.containsProperty(defaultProperty.getKey()))
                .sorted(Comparator.comparing(DefaultProperty::getKey))
                .collect(Collectors.toList());
        // 创建源map
        Map<String, Object> source = new LinkedHashMap<>();
        // 设置spring.application.name属性，如果不存在的话
        if (!environment.containsProperty("spring.application.name")) {
            String applicationName = application.getMainApplicationClass().getSimpleName();
            source.put("spring.application.name", applicationName);
        }
        // 将加载的默认属性添加到源map中
        for (DefaultProperty defaultProperty : loadedDefaultProperties) {
            String key = defaultProperty.getKey();
            String value = defaultProperty.getValue();
            source.put(key, value);
        }
        // 将源map添加为最后一个属性源
        environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, source));
    }


    /**
     * 当 ApplicationEnvironmentPreparedEvent 事件触发时调用的方法
     *
     * @param event ApplicationEnvironmentPreparedEvent 对象，表示 ApplicationEnvironmentPrepared 事件
     */
    @Override
    public void onApplicationEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        for (DefaultProperty defaultProperty : loadedDefaultProperties) {
            String comment = defaultProperty.getComment();
            if (StrUtil.isNotBlank(comment)) {
                // 打印注释行
                System.out.println("\n# " + comment);
            }
            String key = defaultProperty.getKey();
            String value = defaultProperty.getValue();
            if (key.contains("password") && !value.startsWith("${") && !value.startsWith("ENC(")) {
                // 对 password 类型的值进行脱敏处理
                value = DesensitizedUtil.password(value);
            }
            System.out.println(key + "=" + value); // 打印属性键值对
        }
        for (DefaultProperty defaultProperty : loadedDefaultProperties) {
            String comment = defaultProperty.getComment();
            if (StrUtil.isNotBlank(comment)) {
                // 打印空行
                log.info("");
                // 打印注释行
                log.info("# {}", comment);
            }
            String key = defaultProperty.getKey();
            String value = defaultProperty.getValue();
            if (key.contains("password")) {
                // 对 password 类型的值进行脱敏处理
                value = DesensitizedUtil.password(value);
            }
            // 打印属性键值对
            log.info("{}={}", key, value);
        }
    }

    @SneakyThrows
    private Stream<DefaultProperty> formatDefaultEnvs(URL url) {
        // 将URL中的内容读取为列表
        List<DefaultProperty> result = new ArrayList<>();
        String content = IoUtil.readUtf8(url.openStream());
        // 将内容按行切割，并过滤掉空行和去除首尾空格
        List<String> lines = StrUtil.lines(content)
                .filter(StrUtil::isNotBlank)
                .map(StrUtil::trim)
                .collect(Collectors.toList());
        // 上一行注释的评论内容
        String lastComment = "";
        // 遍历每一行内容
        for (String line : lines) {
            // 如果该行以#开头，则保存注释内容，继续下一次循环
            if (line.startsWith("#")) {
                lastComment = StrUtil.subAfter(line, "#", false);
                continue;
            }
            // 如果该行包含=，则该行为key-value形式
            if (line.contains("=")) {
                // 使用=分割行内容，获取key和value
                String key = StrUtil.subBefore(line, "=", false);
                String value = StrUtil.subAfter(line, "=", false);
                // 创建DefaultProperty对象
                DefaultProperty defaultProperty = DefaultProperty.builder()
                        .comment(lastComment) // 使用上一行的注释内容作为该行的注释
                        .key(key)
                        .value(value)
                        .build();
                // 将对象添加到结果列表中
                result.add(defaultProperty);
                lastComment = ""; // 重置注释内容为空
            }
        }
        // 返回结果列表的流
        return result.stream();
    }

}
