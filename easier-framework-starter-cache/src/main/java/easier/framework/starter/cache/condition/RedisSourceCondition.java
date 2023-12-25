package easier.framework.starter.cache.condition;

import easier.framework.core.util.StrUtil;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RedisSourceCondition extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        List<AnnotationAttributes> allAnnotationAttributes = metadata.getAnnotations()
                .stream(ConditionalOnRedisSource.class.getName())
                .filter(MergedAnnotationPredicates.unique(MergedAnnotation::getMetaTypes))
                .map(MergedAnnotation::asAnnotationAttributes)
                .collect(Collectors.toList());
        List<String> allRedisSource = this.getAllRedisSource(context.getEnvironment());
        List<ConditionMessage> noMatch = new ArrayList<>();
        List<ConditionMessage> match = new ArrayList<>();
        for (AnnotationAttributes annotationAttributes : allAnnotationAttributes) {
            String[] values = annotationAttributes.getStringArray("value");
            for (String value : values) {
                if (allRedisSource.contains(value)) {
                    match.add(ConditionMessage.of("已找到缓存源:" + value));
                } else {
                    noMatch.add(ConditionMessage.of("未找到缓存源:" + value));
                }
            }
        }
        if (!noMatch.isEmpty()) {
            return ConditionOutcome.noMatch(ConditionMessage.of(noMatch));
        }
        return ConditionOutcome.match(ConditionMessage.of(match));
    }

    private List<String> getAllRedisSource(Environment environment) {
        String enableRedis = environment.getProperty("spring.easier.cache.enable-redis");
        return StrUtil.smartSplit(enableRedis)
                .stream()
                .flatMap(it -> {
                    String alias = environment.getProperty("spring.easier.cache.redis." + it + ".alias");
                    List<String> list = StrUtil.smartSplit(alias);
                    list.add(it);
                    return list.stream();
                })
                .distinct()
                .collect(Collectors.toList());
    }
}
