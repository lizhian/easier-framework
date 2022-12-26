package tydic.framework.core.plugin.cache.annotation;

import java.lang.annotation.*;

/**
 * 删除缓存
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheDelete {

    /**
     * 实际缓存键={name}:{key}
     */
    String name() default "";

    /**
     * 实际缓存键={name}:{key}
     */
    String key() default "{0}";

    boolean deleteAll() default false;

    /**
     * 缓存源
     */
    String source() default "";
}
