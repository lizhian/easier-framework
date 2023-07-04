package easier.framework.core.plugin.dict;

import java.lang.annotation.*;

/**
 * 字典标记
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface Dict {
    /**
     * 字典编码
     */
    String code();

    /**
     * 字典名称
     */
    String name() default "";

    /**
     * 字典备注
     */
    String remark() default "";

    String property1() default "";

    String property2() default "";

    String property3() default "";

}
