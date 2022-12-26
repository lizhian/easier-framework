package tydic.framework.core.plugin.cache.annotation;

import tydic.framework.core.plugin.cache.enums.LocalCache;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 获取缓存
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheGet {
    /**
     * 实际缓存键={name}:{key}
     */
    String name() default "";

    /**
     * 实际缓存键={name}:{key}
     */
    String key() default "{0}";

    long timeToLive() default 30;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 本地缓存
     */
    LocalCache localCache() default LocalCache.not;

    /**
     * 缓存源
     */
    String source() default "";

}
