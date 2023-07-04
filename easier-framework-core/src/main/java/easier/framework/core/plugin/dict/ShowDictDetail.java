package easier.framework.core.plugin.dict;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JavaType;
import easier.framework.core.plugin.jackson.expland.JsonExpand;
import easier.framework.core.plugin.jackson.expland.JsonExpandContext;
import easier.framework.core.plugin.jackson.expland.JsonExpander;
import easier.framework.core.util.SpringUtil;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 序列化显示用户详情
 */
@JsonExpand(
        expandBy = ShowDictDetail.Expander.class
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ShowDictDetail {

    String value() default "";

    String property() default "{}DictDetail";


    interface ShowDetailDetailBean {
        DictDetail getDictDetail(Object currentValue, String dictCode, JavaType currentType);
    }

    class Expander implements JsonExpander<ShowDictDetail> {

        @Override
        public void doExpand(ShowDictDetail annot, JsonExpandContext context) {
            String key = StrUtil.format(annot.property(), context.getCurrentProperty());
            DictDetail dictDetail = null;
            ShowDetailDetailBean bean = SpringUtil.getAndCache(ShowDetailDetailBean.class);
            if (bean != null) {
                Object currentValue = context.getCurrentValue();
                JavaType currentType = context.getCurrentType();
                dictDetail = bean.getDictDetail(currentValue, annot.value(), currentType);
            }
            context.write(key, dictDetail);
        }
    }
}
