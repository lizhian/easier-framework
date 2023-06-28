package easier.framework.starter.innerRequest;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(easier.framework.starter.innerRequest.InnerRequestAutoConfiguration.class)
public @interface EnableInnerRequest {
}
