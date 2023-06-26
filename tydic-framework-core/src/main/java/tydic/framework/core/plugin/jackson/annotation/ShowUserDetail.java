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
 * 序列化显示用户详情
 */
@JsonExpand(
        expandBy = ShowUserDetail.Expander.class
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ShowUserDetail {

    String value() default "{}_UserDetail";

    interface ShowUserDetailExpanderBean {
        String getUserDetail(Object fieldValue);
    }

    class Expander implements JsonExpander<ShowUserDetail> {

        @Override
        public void doExpand(ShowUserDetail annot, JsonExpandContext context) {
            String key = StrUtil.format(annot.value(), context.getCurrentProperty());
            Object userDetail = null;
            ShowUserDetailExpanderBean bean = SpringUtil.getAndCache(ShowUserDetailExpanderBean.class);
            if (bean != null) {
                Object currentValue = context.getCurrentValue();
                userDetail = bean.getUserDetail(currentValue);
            }
            context.write(key, userDetail);
        }
    }
}
