package tydic.framework.starter.web.converter;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class TimeConverters {
    public static final Converter<String, Date> stringToDate = new StringToDateConverter();
    public static final Converter<String, DateTime> stringToDateTime = new StringToDateTimeConverter();
    public static final Converter<String, LocalTime> stringToLocalTime = new StringToLocalTimeConverter();
    public static final Converter<String, LocalDate> stringToLocalDate = new StringToLocalDateConverter();
    public static final Converter<String, LocalDateTime> stringToLocalDateTime = new StringToLocalDateTimeConverter();


    static class StringToDateConverter implements Converter<String, Date> {
        @Override
        public Date convert(String source) {
            if (StrUtil.isBlank(source)) {
                return null;
            }
            return DateUtil.parse(source).toJdkDate();
        }
    }


    static class StringToDateTimeConverter implements Converter<String, DateTime> {
        @Override
        public DateTime convert(String source) {
            if (StrUtil.isBlank(source)) {
                return null;
            }
            return DateUtil.parse(source);
        }
    }

    static class StringToLocalTimeConverter implements Converter<String, LocalTime> {
        @Override
        public LocalTime convert(String source) {
            if (StrUtil.isBlank(source)) {
                return null;
            }
            DateTime time = DateUtil.parse(source);
            return DateUtil.toLocalDateTime(time).toLocalTime();
        }
    }

    static class StringToLocalDateConverter implements Converter<String, LocalDate> {
        @Override
        public LocalDate convert(String source) {
            if (StrUtil.isBlank(source)) {
                return null;
            }
            DateTime time = DateUtil.parse(source);
            return DateUtil.toLocalDateTime(time).toLocalDate();
        }
    }

    static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(String source) {
            if (StrUtil.isBlank(source)) {
                return null;
            }
            DateTime time = DateUtil.parse(source);
            return DateUtil.toLocalDateTime(time);
        }
    }


}
