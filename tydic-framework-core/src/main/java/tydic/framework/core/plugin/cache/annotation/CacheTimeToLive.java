package tydic.framework.core.plugin.cache.annotation;

import java.lang.annotation.*;

/**
 * 标记缓存过期时间值
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheTimeToLive {
}
