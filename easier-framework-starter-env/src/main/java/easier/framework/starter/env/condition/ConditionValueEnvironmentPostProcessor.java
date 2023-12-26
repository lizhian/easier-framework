package easier.framework.starter.env.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

/**
 * 条件值环境后处理器
 */
@Slf4j
public class ConditionValueEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 10;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 获取属性源
        MutablePropertySources sources = environment.getPropertySources();
        // 获取已存在的属性源
        PropertySource<?> existing = sources.get(ConditionValuePropertySource.SOURCE_NAME);
        if (existing != null) {
            // 如果已存在，则返回
            log.debug("【ConditionValuePropertySource】已存在");
            return;
        }
        // 创建 ConditionValuePropertySource 对象
        ConditionValuePropertySource conditionSource = new ConditionValuePropertySource();
        // 如果存在系统环境变量属性源，则将 ConditionValuePropertySource 放在其后面
        if (sources.get(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME) != null) {
            sources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, conditionSource);
        } else {
            sources.addLast(conditionSource);
        }
        log.debug("【ConditionValuePropertySource】已添加");
    }
}
