package easier.framework.starter.logging;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EasierLoggingAutoConfiguration.class)
public @interface EnableEasierLogging {
}
