package easier.framework.starter.doc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({EasierDocAutoConfiguration.class})
public @interface EnableEasierDoc {
}
