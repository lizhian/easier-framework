package tydic.framework.starter.jackson;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tydic.framework.core.plugin.jackson.ObjectMapperHolder;
import tydic.framework.starter.jackson.module.EasierJacksonModule;


@Configuration(proxyBeanMethods = false)
@Import({
        ObjectMapperHolder.class
        , EasierJacksonModule.class
})
public class TydicJacksonAutoConfiguration {

    /*@Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            Locale china = Locale.CHINA;
            builder.locale(china);
            TimeZone timeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
            builder.timeZone(timeZone);
        };
    }*/
}
