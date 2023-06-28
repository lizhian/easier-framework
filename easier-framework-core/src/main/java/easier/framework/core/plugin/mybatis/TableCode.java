
package easier.framework.core.plugin.mybatis;


import java.lang.annotation.*;

/**
 * 编码字段标识
 * 作用在 xxxByCode 方法上
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface TableCode {

}
