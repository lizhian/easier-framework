package easier.framework.starter.env;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

@Slf4j
public class ConditionValueEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 10;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources sources = environment.getPropertySources();
        PropertySource<?> existing = sources.get(ConditionValuePropertySource.SOURCE_NAME);
        if (existing != null) {
            log.debug("ConditionValuePropertySource already present");
            return;
        }
        ConditionValuePropertySource conditionSource = new ConditionValuePropertySource();
        if (sources.get(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME) != null) {
            sources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, conditionSource);
        } else {
            sources.addLast(conditionSource);
        }
        log.debug("ConditionValuePropertySource add to Environment");
    }

}
