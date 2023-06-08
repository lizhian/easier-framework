package tydic.framework.core.plugin.jackson.expland;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Field;

@Data
@FieldNameConstants
@SuperBuilder(toBuilder = true)
public class JsonExpandContext {
    private final JsonGenerator jsonGenerator;
    private final Class<?> beanClass;
    private final Object beanValue;
    private final Field field;
    private final Class<?> fieldType;
    private final String fieldName;
    private final Object fieldValue;

    @SneakyThrows
    public void write(String fieldName, Object fieldValue) {
        if (StrUtil.isBlank(fieldName)) {
            return;
        }
        if (fieldValue == null) {
            this.jsonGenerator.writeNullField(fieldName);
            return;
        }
        if (fieldValue instanceof String) {
            this.jsonGenerator.writeStringField(fieldName, (String) fieldValue);
            return;
        }
        this.jsonGenerator.writeObjectField(fieldName, fieldValue);
    }

}
