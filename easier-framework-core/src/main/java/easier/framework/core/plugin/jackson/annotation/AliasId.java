package easier.framework.core.plugin.jackson.annotation;

import easier.framework.core.plugin.jackson.expland.JsonExpand;
import easier.framework.core.plugin.jackson.expland.JsonExpandContext;
import easier.framework.core.plugin.jackson.expland.JsonExpander;

import java.lang.annotation.*;

/**
 * 序列化增加 id 属性
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@JsonExpand(expandBy = AliasId.Expander.class)
public @interface AliasId {

    /**
     * 反序列化的时候优先从[id]获取值
     */
    boolean deserializeFirst() default false;

    class Expander implements JsonExpander<AliasId> {
        public static String ID = "id";

        @Override
        public void doExpand(AliasId annot, JsonExpandContext context) {
            Object value = context.getCurrentValue();
            context.write(ID, value);
        }
    }
}