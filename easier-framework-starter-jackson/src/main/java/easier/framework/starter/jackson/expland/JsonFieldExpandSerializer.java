package easier.framework.starter.jackson.expland;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import easier.framework.core.plugin.jackson.expland.JsonExpandContext;
import easier.framework.core.plugin.jackson.expland.JsonExpander;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @Author: tgd
 * @Date: 2022/6/15 2:55 PM
 */


@Slf4j
public class JsonFieldExpandSerializer extends StdSerializer<Object> implements ContextualSerializer {
    private final List<JsonFieldExpandDetail> jsonFieldExpandDetails;
    private final JavaType javaType;

    public JsonFieldExpandSerializer(JavaType type, List<JsonFieldExpandDetail> jsonFieldExpandDetails) {
        super(type);
        this.javaType = type;
        this.jsonFieldExpandDetails = jsonFieldExpandDetails;
    }

    @SneakyThrows
    @Override
    public void serialize(Object value, JsonGenerator generator, SerializerProvider provider) {
        if (value == null) {
            generator.writeNull();
        } else {
            generator.writeObject(value);
        }
        if (CollUtil.isEmpty(this.jsonFieldExpandDetails)) {
            return;
        }
        JsonStreamContext outputContext = generator.getOutputContext();
        String currentName = generator.getOutputContext().getCurrentName();
        JsonExpandContext context = JsonExpandContext.builder()
                .generator(generator)
                .provider(provider)
                .outputContext(outputContext)
                .currentProperty(currentName)
                .currentValue(value)
                .currentType(javaType)
                .build();
        for (JsonFieldExpandDetail detail : this.jsonFieldExpandDetails) {
            Annotation annotation = detail.getAnnotation();
            for (JsonExpander expander : detail.getExpanders()) {
                expander.doExpand(annotation, context);
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return this;
    }
}
