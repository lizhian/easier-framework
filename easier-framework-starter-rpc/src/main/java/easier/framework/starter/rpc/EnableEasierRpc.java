package easier.framework.starter.rpc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用更简单rpc
 *
 * @author lizhian
 * @date 2023年07月16日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EasierRpcAutoConfiguration.class)
public @interface EnableEasierRpc {
}
