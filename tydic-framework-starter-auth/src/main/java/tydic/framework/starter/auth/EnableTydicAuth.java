package tydic.framework.starter.auth;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(TydicAuthAutoConfiguration.class)
public @interface EnableTydicAuth {
}
