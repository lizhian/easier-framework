package tydic.framework.core.plugin.auth.annotation;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import tydic.framework.core.plugin.auth.AuthContext;
import tydic.framework.core.plugin.auth.expand.AuthExpand;
import tydic.framework.core.plugin.auth.expand.AuthExpandContext;
import tydic.framework.core.plugin.auth.expand.AuthExpander;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AuthExpand(
        expandBy = MustPermission.Expander.class
)
public @interface MustPermission {
    String value();

    @Slf4j
    class Expander implements AuthExpander<MustPermission> {
        @Override
        public void doExpand(MustPermission mustPermission, AuthExpandContext context) {
            if (context.hasAnnotation(IgnoreAuth.class)) {
                return;
            }
            if (AuthContext.isInnerRequest()) {
                return;
            }
            AuthContext.mustLogin();
            if (AuthContext.isAdmin()) {
                return;
            }
            StpUtil.checkPermission(mustPermission.value());
        }
    }
}
