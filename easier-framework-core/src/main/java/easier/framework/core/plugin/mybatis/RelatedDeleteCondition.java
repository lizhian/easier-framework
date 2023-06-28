package easier.framework.core.plugin.mybatis;

import java.lang.annotation.*;

/**
 * 关联删除字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RelatedDeleteCondition {
    Class<?> source();

    String sourceField();
}
