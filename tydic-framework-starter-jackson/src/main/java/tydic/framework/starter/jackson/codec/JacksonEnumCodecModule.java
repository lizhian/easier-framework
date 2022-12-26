package tydic.framework.starter.jackson.codec;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import lombok.RequiredArgsConstructor;
import tydic.framework.core.plugin.enums.EnumCodec;
import tydic.framework.core.util.InstanceUtil;

import java.io.IOException;

/**
 * 枚举转换器
 */
public class JacksonEnumCodecModule extends Module {

    @Override
    public String getModuleName() {
        return this.getClass().getName();
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(new EnumSerializerModifier());
        context.addBeanDeserializerModifier(new EnumDeserializerModifier());
    }

    static class EnumSerializerModifier extends BeanSerializerModifier {
        @Override
        public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config,
                                                      JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            return InstanceUtil.in(EnumSerializerModifier.class)
                               .getInstance(valueType, () -> new EnumCodecSerializer<>(EnumCodec.of(valueType.getRawClass())));
        }
    }

    static class EnumDeserializerModifier extends BeanDeserializerModifier {

        @Override
        public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config,
                                                          JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
            return InstanceUtil.in(EnumDeserializerModifier.class)
                               .getInstance(type, () -> new EnumCodecDeserializer<>(EnumCodec.of(type.getRawClass())));
        }
    }

    @RequiredArgsConstructor
    public static class EnumCodecSerializer<E extends Enum<E>> extends JsonSerializer<E> {
        private final EnumCodec<E> enumCodec;

        @Override
        public void serialize(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (this.enumCodec.isInt()) {
                gen.writeNumber(this.enumCodec.enum2ValueAsInt(value));
            } else {
                gen.writeString(this.enumCodec.enum2ValueAsString(value));
            }
        }
    }

    @RequiredArgsConstructor
    public static class EnumCodecDeserializer<E extends Enum<E>> extends JsonDeserializer<E> {
        private final EnumCodec<E> enumCodec;

        @Override
        public E deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return this.enumCodec.value2Enum(p.getValueAsString());
        }
    }
}
