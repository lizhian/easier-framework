package easier.framework.core.plugin.auth.annotation;

import java.lang.annotation.*;

/**
 * 标记为基础查询权限
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GetPermission {
}
