package easier.framework.core.plugin.jackson.annotation;

import cn.hutool.core.util.StrUtil;
import easier.framework.core.plugin.jackson.expland.JsonExpand;
import easier.framework.core.plugin.jackson.expland.JsonExpandContext;
import easier.framework.core.plugin.jackson.expland.JsonExpander;
import easier.framework.core.util.SpringUtil;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 序列化显示部门详情
 */
@JsonExpand(
        expandBy = ShowDeptDetail.Expander.class
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ShowDeptDetail {

    String value() default "{}_DeptDetail";

    interface ShowDeptDetailBean {
        Object getDeptDetail(Object fieldValue);
    }

    class Expander implements JsonExpander<ShowDeptDetail> {

        @Override
        public void doExpand(ShowDeptDetail annot, JsonExpandContext context) {
            String key = StrUtil.format(annot.value(), context.getCurrentProperty());
            Object detail = "未支持获取部门详情";
            ShowDeptDetailBean bean = SpringUtil.getAndCache(ShowDeptDetail.ShowDeptDetailBean.class);
            if (bean != null) {
                Object currentValue = context.getCurrentValue();
                detail = bean.getDeptDetail(currentValue);
            }
            context.write(key, detail);
        }
    }
}
