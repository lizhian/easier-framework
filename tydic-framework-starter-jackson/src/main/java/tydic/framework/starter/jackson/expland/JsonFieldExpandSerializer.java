package tydic.framework.starter.jackson.expland;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tydic.framework.core.plugin.jackson.expland.JsonExpandContext;
import tydic.framework.core.plugin.jackson.expland.JsonExpander;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
    public void serialize(Object fieldValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        if (fieldValue == null) {
            jsonGenerator.writeNull();
            return;
        }
        jsonGenerator.writeObject(fieldValue);
        if (CollUtil.isEmpty(this.jsonFieldExpandDetails)) {
            return;
        }

        Object beanValue = jsonGenerator.getOutputContext().getCurrentValue();
        Class<?> beanClass = beanValue.getClass();
        String fieldName = jsonGenerator.getOutputContext().getCurrentName();
        Field field = ReflectUtil.getField(beanClass, fieldName);
        Class<?> fieldType = field.getType();
        JsonExpandContext context = JsonExpandContext.builder()
                .jsonGenerator(jsonGenerator)
                .beanClass(beanClass)
                .beanValue(beanValue)
                .field(field)
                .fieldType(fieldType)
                .fieldName(fieldName)
                .fieldValue(fieldValue)
                .build();
        for (JsonFieldExpandDetail detail : this.jsonFieldExpandDetails) {
            Annotation annotation = detail.getAnnotation();
            for (JsonExpander expander : detail.getExpanders()) {
                expander.doExpand(annotation, context);
            }
        }

        /*String targetProperty = this.targetProperty(field, fieldName, fieldValue);
        TimeInterval timer = DateUtil.timer();
        if (targetPropertyValue == null) {
            jsonGenerator.writeNullField(targetProperty);
            return;
        }
        //log.info("{}:{}->{},耗时:{}", this.getClass().getSimpleName(), fieldValue, targetPropertyValue, timer.intervalPretty());
        jsonGenerator.writeObjectField(targetProperty, targetPropertyValue);*/
    }


}
