package easier.framework.core.plugin.auth.annotation;

import cn.dev33.satoken.stp.StpUtil;
import easier.framework.core.Easier;
import easier.framework.core.plugin.auth.expand.AuthExpand;
import easier.framework.core.plugin.auth.expand.AuthExpandContext;
import easier.framework.core.plugin.auth.expand.AuthExpander;
import lombok.extern.slf4j.Slf4j;

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
            if (Easier.Auth.isSafeRequest()) {
                return;
            }
            Easier.Auth.mustLogin();
            if (Easier.Auth.isAdmin()) {
                return;
            }
            StpUtil.checkPermission(mustPermission.value());
        }
    }
}
