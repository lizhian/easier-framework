package tydic.framework.starter.mybatis;

import com.tangzc.mpe.autotable.EnableAutoTable;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(TydicMybatisAutoConfiguration.class)
@EnableAutoTable(activeProfile = {"test", "dev"})
public @interface EnableTydicMybatis {
}
