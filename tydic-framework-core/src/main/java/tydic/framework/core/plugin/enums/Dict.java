package tydic.framework.core.plugin.enums;

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
     * 字典类型编码
     */
    String type();

    /**
     * 字典类型名称
     */
    String name() default "";

    String expandField1() default "";

    String expandField2() default "";

    String expandField3() default "";

}
