package easier.framework.core.plugin.jackson.expland;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@FieldNameConstants
@SuperBuilder(toBuilder = true)
public class JsonExpandContext {
    private final JsonGenerator generator;
    private final SerializerProvider provider;
    private final JsonStreamContext outputContext;
    private final String currentProperty;
    private final Object currentValue;
    private final JavaType currentType;

    @SneakyThrows
    public void write(String key, Object value) {
        if (StrUtil.isBlank(key)) {
            return;
        }
        if (value == null) {
            this.generator.writeNullField(key);
            return;
        }
        if (value instanceof String) {
            this.generator.writeStringField(key, (String) value);
            return;
        }
        this.generator.writeObjectField(key, value);
    }

}
