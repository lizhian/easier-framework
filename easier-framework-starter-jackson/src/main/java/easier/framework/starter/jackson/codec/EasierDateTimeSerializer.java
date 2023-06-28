package easier.framework.starter.jackson.codec;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase;

import java.io.IOException;
import java.text.DateFormat;

@JacksonStdImpl
public class EasierDateTimeSerializer extends DateTimeSerializerBase<DateTime> {

    public final static EasierDateTimeSerializer instance = new EasierDateTimeSerializer(null, null);

    protected EasierDateTimeSerializer(Boolean useTimestamp, DateFormat customFormat) {
        super(DateTime.class, useTimestamp, customFormat);
    }

    @Override
    public DateTimeSerializerBase<DateTime> withFormat(Boolean timestamp, DateFormat customFormat) {
        return new EasierDateTimeSerializer(timestamp, customFormat);
    }

    @Override
    protected long _timestamp(DateTime value) {
        return value == null ? 0L : value.getTime();
    }

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (super._asTimestamp(provider)) {
            gen.writeNumber(_timestamp(value));
            return;
        }
        if (super._customFormat == null) {
            gen.writeString(value.toString());
            return;
        }
        super._serializeAsString(value.toJdkDate(), gen, provider);
    }


}
