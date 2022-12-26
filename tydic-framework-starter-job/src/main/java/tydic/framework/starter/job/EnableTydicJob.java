package tydic.framework.starter.job;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(TydicJobAutoConfiguration.class)
public @interface EnableTydicJob {
}
