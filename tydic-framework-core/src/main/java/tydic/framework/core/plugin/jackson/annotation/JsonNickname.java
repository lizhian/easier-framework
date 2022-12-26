package tydic.framework.core.plugin.jackson.annotation;

import cn.hutool.core.util.StrUtil;
import tydic.framework.core.plugin.jackson.expland.JsonExpand;
import tydic.framework.core.plugin.jackson.expland.JsonExpandContext;
import tydic.framework.core.plugin.jackson.expland.JsonExpander;
import tydic.framework.core.util.SpringUtil;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 序列化增加用户名称
 */
@JsonExpand(
        expandBy = JsonNickname.Expander.class
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface JsonNickname {

    String value() default "";

    interface JsonNicknameExpanderBean {
        String getNickname(Object fieldValue);
    }

    class Expander implements JsonExpander<JsonNickname> {

        @Override
        public void doExpand(JsonNickname jsonNickname, JsonExpandContext context) {
            String fieldName = context.getFieldName() + "Nickname";
            if (StrUtil.isNotBlank(jsonNickname.value())) {
                fieldName = jsonNickname.value();
            }
            String nickname = "";
            JsonNicknameExpanderBean bean = SpringUtil.getAndCache(JsonNicknameExpanderBean.class);
            if (bean != null) {
                Object fieldValue = context.getFieldValue();
                nickname = bean.getNickname(fieldValue);
            }
            context.write(fieldName, nickname);
        }
    }
}
