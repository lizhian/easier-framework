package tydic.framework.core.plugin.jackson.annotation;

import tydic.framework.core.plugin.jackson.expland.JsonExpand;
import tydic.framework.core.plugin.jackson.expland.JsonExpandContext;
import tydic.framework.core.plugin.jackson.expland.JsonExpander;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 序列化显示显示布尔值的相反值
 */
@JsonExpand(
        expandBy = BoolReverse.Expander.class
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BoolReverse {
    String value();

    class Expander implements JsonExpander<BoolReverse> {

        @Override
        public void doExpand(BoolReverse annot, JsonExpandContext context) {
            String key = annot.value();
            Object currentValue = context.getCurrentValue();
            if (currentValue == null) {
                context.write(key, null);
                return;
            }
            if (currentValue.toString().equalsIgnoreCase(Boolean.TRUE.toString())) {
                context.write(key, false);
                return;
            }
            if (currentValue.toString().equalsIgnoreCase(Boolean.FALSE.toString())) {
                context.write(key, true);
            }
        }
    }
}
