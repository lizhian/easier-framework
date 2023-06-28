package easier.framework.core.plugin.jackson.expland;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * json拓展注解
 */
@Documented
@Target({ANNOTATION_TYPE})
@Retention(RUNTIME)
@Inherited
public @interface JsonExpand {
    Class<? extends JsonExpander<?>>[] expandBy();
}

