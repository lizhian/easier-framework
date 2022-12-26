package tydic.framework.starter.cache;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用缓存功能
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(TydicCacheAutoConfiguration.class)
public @interface EnableTydicCache {
}
