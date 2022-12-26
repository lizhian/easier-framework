package tydic.framework.core.plugin.jackson.annotation;

import tydic.framework.core.plugin.jackson.expland.JsonExpand;
import tydic.framework.core.plugin.jackson.expland.JsonExpandContext;
import tydic.framework.core.plugin.jackson.expland.JsonExpander;

import java.lang.annotation.*;

/**
 * 序列化增加 id 属性
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@JsonExpand(expandBy = JsonID.Expander.class)
public @interface JsonID {
    class Expander implements JsonExpander<JsonID> {

        @Override
        public void doExpand(JsonID annot, JsonExpandContext context) {
            Object fieldValue = context.getFieldValue();
            context.write("id", fieldValue);
        }
    }
}
