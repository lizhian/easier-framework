package easier.framework.application;

import easier.framework.starter.cache.EnableEasierCache;
import easier.framework.starter.job.EnableEasierJob;
import easier.framework.starter.logging.EnableEasierLogging;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableEasierCache
@EnableEasierJob
@EnableEasierLogging
public @interface EasierApplication {
}
