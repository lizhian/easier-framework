package tydic.framework.core.plugin.auth.annotation;

import java.lang.annotation.*;

/**
 * 标记为基础删除权限
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DeletePermission {
}
