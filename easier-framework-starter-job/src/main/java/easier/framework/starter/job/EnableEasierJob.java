package easier.framework.starter.job;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EasierJobAutoConfiguration.class)
public @interface EnableEasierJob {
}
