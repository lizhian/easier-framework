package easier.framework.starter.auth;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(easier.framework.starter.auth.EasierAuthAutoConfiguration.class)
public @interface EnableEasierAuth {
}
