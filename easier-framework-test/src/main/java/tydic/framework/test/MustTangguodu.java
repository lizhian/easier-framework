package easier.framework.test;

import easier.framework.core.plugin.auth.AuthContext;
import easier.framework.core.plugin.auth.annotation.IgnoreAuth;
import easier.framework.core.plugin.auth.expand.AuthExpand;
import easier.framework.core.plugin.auth.expand.AuthExpandContext;
import easier.framework.core.plugin.auth.expand.AuthExpander;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AuthExpand(
        expandBy = MustTangguodu.Expander.class
)
public @interface MustTangguodu {

    class Expander implements AuthExpander<MustTangguodu> {
        @Override
        public void doExpand(MustTangguodu mustLogin, AuthExpandContext context) {
            if (context.hasAnnotation(IgnoreAuth.class)) {
                return;
            }
            if (AuthContext.isInnerRequest()) {
                return;
            }
            AuthContext.mustLogin();
        }
    }
}
