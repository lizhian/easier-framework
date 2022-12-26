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
        expandBy = JsonSerializeAlias.Expander.class
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface JsonSerializeAlias {

    String[] value();

    class Expander implements JsonExpander<JsonSerializeAlias> {

        @Override
        public void doExpand(JsonSerializeAlias alias, JsonExpandContext context) {
            Object fieldValue = context.getFieldValue();
            for (String newName : alias.value()) {
                context.write(newName, fieldValue);
            }
        }
    }
}
