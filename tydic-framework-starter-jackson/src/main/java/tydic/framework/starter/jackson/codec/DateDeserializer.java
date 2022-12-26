package tydic.framework.starter.jackson.codec;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

@Slf4j
@JacksonStdImpl
public class DateDeserializer extends StdScalarDeserializer<Date> {

    public final static DateDeserializer instance = new DateDeserializer();

    private DateDeserializer() {
        super(Date.class);
    }


    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        if (StrUtil.isBlank(value)) {
            return null;
        }
        if (NumberUtil.isNumber(value)) {
            return new Date(Long.parseLong(value));
        }
        return DateUtil.parse(value).toJdkDate();
    }
}
