package easier.framework.starter.mybatis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用更简单mybatis
 *
 * @author lizhian
 * @date 2023年07月05日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EasierMybatisAutoConfiguration.class)
public @interface EnableEasierMybatis {
}
