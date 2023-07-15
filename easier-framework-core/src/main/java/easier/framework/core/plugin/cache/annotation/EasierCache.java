package easier.framework.core.plugin.cache.annotation;

import easier.framework.core.plugin.cache.enums.LocalCache;

import java.lang.annotation.*;

/**
 * 缓存参数
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EasierCache {

    String source() default "";

    String prefix();

    long timeToLiveSeconds();

    LocalCache localCache() default LocalCache.not;


}
