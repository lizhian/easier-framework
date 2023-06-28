package easier.framework.core.plugin.auth.annotation;

import easier.framework.core.plugin.auth.AuthContext;
import easier.framework.core.plugin.auth.expand.AuthExpand;
import easier.framework.core.plugin.auth.expand.AuthExpandContext;
import easier.framework.core.plugin.auth.expand.AuthExpander;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AuthExpand(
        expandBy = MustLogin.Expander.class
)
public @interface MustLogin {

    class Expander implements AuthExpander<MustLogin> {
        @Override
        public void doExpand(MustLogin mustLogin, AuthExpandContext context) {
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
