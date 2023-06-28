package easier.framework.starter.jackson;

import easier.framework.core.plugin.jackson.ObjectMapperHolder;
import easier.framework.starter.jackson.module.EasierJacksonModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration(proxyBeanMethods = false)
@Import({
        ObjectMapperHolder.class
        , EasierJacksonModule.class
})
public class EasierJacksonAutoConfiguration {

    /*@Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.annotationIntrospector(old -> {
                if (old == null) {
                    return EasierAnnotationIntrospector.INSTANCE;
                }
                return AnnotationIntrospector.pair(EasierAnnotationIntrospector.INSTANCE, old);
            });
        };
    }*/
}
