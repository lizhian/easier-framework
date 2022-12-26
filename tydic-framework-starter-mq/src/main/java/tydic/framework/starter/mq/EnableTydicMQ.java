package tydic.framework.starter.mq;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(TydicMQAutoConfiguration.class)
public @interface EnableTydicMQ {
}
