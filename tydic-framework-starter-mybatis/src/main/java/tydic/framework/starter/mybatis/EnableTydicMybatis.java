package tydic.framework.starter.mybatis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(TydicMybatisAutoConfiguration.class)
public @interface EnableTydicMybatis {
}
