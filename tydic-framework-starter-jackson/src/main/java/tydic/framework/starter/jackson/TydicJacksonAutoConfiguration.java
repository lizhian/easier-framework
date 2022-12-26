package tydic.framework.starter.jackson;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tydic.framework.core.plugin.jackson.ObjectMapperHolder;
import tydic.framework.starter.jackson.codec.*;
import tydic.framework.starter.jackson.expland.JacksonAnnotationExpander;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Configuration(proxyBeanMethods = false)
@Import({
        ObjectMapperHolder.class
        , JacksonEnumCodecModule.class
})
public class TydicJacksonAutoConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        DateTimeFormatter time = DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN);
        DateTimeFormatter date = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        return builder -> {
            builder.locale(Locale.CHINA);
            builder.timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            builder.simpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
            //long
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(long.class, ToStringSerializer.instance);
            //Date
            builder.serializerByType(Date.class, DateSerializer.instance);
            builder.deserializerByType(Date.class, DateDeserializer.instance);
            //DateTime
            builder.serializerByType(DateTime.class, DateTimeSerializer.instance);
            builder.deserializerByType(DateTime.class, DateTimeDeserializer.instance);
            //LocalTime
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(time));
            builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(time));
            //LocalDate
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(date));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(date));
            //LocalDateTime
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(dateTime));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(dateTime));
            //@JsonInject支持
            builder.annotationIntrospector(new JacksonAnnotationExpander());
        };
    }
}
