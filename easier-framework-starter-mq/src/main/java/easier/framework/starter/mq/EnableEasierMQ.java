package easier.framework.starter.mq;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(easier.framework.starter.mq.EasierMQAutoConfiguration.class)
public @interface EnableEasierMQ {
}
