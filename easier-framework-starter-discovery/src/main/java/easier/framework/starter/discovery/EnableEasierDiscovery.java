package easier.framework.starter.discovery;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用服务发现功能
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EasierDiscoveryAutoConfiguration.class)
@EnableDiscoveryClient
public @interface EnableEasierDiscovery {
}
