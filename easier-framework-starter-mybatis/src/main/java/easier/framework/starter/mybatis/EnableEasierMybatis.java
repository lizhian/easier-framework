package easier.framework.starter.mybatis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EasierMybatisAutoConfiguration.class)
public @interface EnableEasierMybatis {
}
