package easier.framework.application.web;

import easier.framework.application.EasierApplication;
import easier.framework.starter.doc.EnableEasierDoc;
import easier.framework.starter.web.EnableEasierWeb;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EasierApplication
@EnableEasierWeb
@EnableEasierDoc
public @interface EasierWebApplication {
}
