package easier.framework.core.plugin.auth.annotation;

import cn.dev33.satoken.stp.StpUtil;
import easier.framework.core.Easier;
import easier.framework.core.plugin.auth.expand.AuthExpand;
import easier.framework.core.plugin.auth.expand.AuthExpandContext;
import easier.framework.core.plugin.auth.expand.AuthExpander;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AuthExpand(
        expandBy = MustRole.Expander.class
)
public @interface MustRole {
    String value();

    class Expander implements AuthExpander<MustRole> {
        @Override
        public void doExpand(MustRole mustRole, AuthExpandContext context) {
            if (context.hasAnnotation(IgnoreAuth.class)) {
                return;
            }
            if (Easier.Auth.isSafeRequest()) {
                return;
            }
            Easier.Auth.mustLogin();
            if (Easier.Auth.isAdmin()) {
                return;
            }
            StpUtil.checkRole(mustRole.value());
        }
    }
}
