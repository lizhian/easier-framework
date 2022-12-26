package tydic.framework.starter.jackson.codec;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

@Slf4j
@JacksonStdImpl
public class DateSerializer extends StdScalarSerializer<Date> {

    public final static DateSerializer instance = new DateSerializer();

    private DateSerializer() {
        super(Date.class);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeString("");
            return;

        }
        gen.writeString(DateUtil.formatDateTime(value));
    }
}
