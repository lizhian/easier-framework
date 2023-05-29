package tydic.framework.core.plugin.auth.annotation;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import tydic.framework.core.plugin.auth.AuthContext;
import tydic.framework.core.plugin.auth.expand.AuthExpand;
import tydic.framework.core.plugin.auth.expand.AuthExpandContext;
import tydic.framework.core.plugin.auth.expand.AuthExpander;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.*;
import java.util.List;

/**
 * 基础权限检验器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AuthExpand(
        expandBy = BaseAuth.Expander.class
)
public @interface BaseAuth {
    String value();

    class Expander implements AuthExpander<BaseAuth> {
        @Override
        public void doExpand(BaseAuth baseAuth, AuthExpandContext context) {
            if (context.hasAnnotation(IgnoreAuth.class)) {
                return;
            }
            if (AuthContext.isInnerRequest()) {
                return;
            }
            List<Annotation> otherAuthAnnotation = context.getOtherAuthAnnotations();
            if (CollUtil.isNotEmpty(otherAuthAnnotation)) {
                return;
            }
            AuthContext.mustLogin();
            if (AuthContext.isAdmin()) {
                return;
            }
            String permission = baseAuth.value() + "#" + this.getMethodType(context);
            StpUtil.checkPermission(permission);
        }

        private String getMethodType(AuthExpandContext context) {
            HttpServletRequest request = context.getRequest();
            if (context.hasAnnotation(GetPermission.class) || request.getMethod().equalsIgnoreCase("get")) {
                return "Get";
            }
            if (context.hasAnnotation(DeletePermission.class) || request.getMethod().equalsIgnoreCase("delete")) {
                return "Delete";
            }
            return "Edit";
        }
    }
}
