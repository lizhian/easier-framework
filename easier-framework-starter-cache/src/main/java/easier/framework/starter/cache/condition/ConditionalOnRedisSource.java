package easier.framework.starter.cache.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(RedisSourceCondition.class)
public @interface ConditionalOnRedisSource {
    String[] value();
}
