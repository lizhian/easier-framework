package tydic.framework.core.plugin.enums;

import java.lang.annotation.*;

/**
 * 枚举描述
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface EnumLabel {
}
