package tydic.framework.core.plugin.cache.annotation;

import java.lang.annotation.*;

/**
 * 标记缓存值
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheValue {
}
