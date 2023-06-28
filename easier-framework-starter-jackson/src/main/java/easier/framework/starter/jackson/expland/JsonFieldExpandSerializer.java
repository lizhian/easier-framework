package easier.framework.starter.jackson.expland;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
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
public class JsonFieldExpandSerializer extends StdSerializer<Object> {
    private final List<JsonFieldExpandDetail> jsonFieldExpandDetails;

    public JsonFieldExpandSerializer(JavaType type, List<JsonFieldExpandDetail> jsonFieldExpandDetails) {
        super(type);
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
                .build();
        for (JsonFieldExpandDetail detail : this.jsonFieldExpandDetails) {
            Annotation annotation = detail.getAnnotation();
            for (JsonExpander expander : detail.getExpanders()) {
                expander.doExpand(annotation, context);
            }
        }
    }
}
