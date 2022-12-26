package tydic.framework.starter.logging;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(TydicLoggingAutoConfiguration.class)
public @interface EnableTydicLogging {
    boolean showTraceId() default true;
}
