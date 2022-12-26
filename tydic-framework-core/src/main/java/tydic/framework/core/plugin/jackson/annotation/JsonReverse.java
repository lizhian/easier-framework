package tydic.framework.core.plugin.jackson.annotation;

import tydic.framework.core.plugin.jackson.expland.JsonExpand;
import tydic.framework.core.plugin.jackson.expland.JsonExpandContext;
import tydic.framework.core.plugin.jackson.expland.JsonExpander;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 序列化增加别名
 */
@JsonExpand(
        expandBy = JsonReverse.Expander.class
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface JsonReverse {
    String value();

    class Expander implements JsonExpander<JsonReverse> {

        @Override
        public void doExpand(JsonReverse annot, JsonExpandContext context) {
            Object fieldValue = context.getFieldValue();
            if (fieldValue == null) {
                context.write(annot.value(), null);
                return;
            }
            if (fieldValue.toString().equalsIgnoreCase(Boolean.TRUE.toString())) {
                context.write(annot.value(), false);
                return;
            }
            if (fieldValue.toString().equalsIgnoreCase(Boolean.FALSE.toString())) {
                context.write(annot.value(), true);
            }
        }
    }
}
