package tydic.framework.starter.jackson.module;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tydic.framework.core.plugin.enums.EnumCodec;
import tydic.framework.starter.jackson.codec.EasierEnumDeserializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EasierBeanDeserializerModifier extends BeanDeserializerModifier {
    public static final EasierBeanDeserializerModifier INSTANCE = new EasierBeanDeserializerModifier();
    private final static Map<Class<?>, JsonDeserializer<?>> ENUM_DESERIALIZERS = new ConcurrentHashMap<>();


    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config,
                                                      JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        Class<?> clazz = type.getRawClass();
        return ENUM_DESERIALIZERS.computeIfAbsent(clazz, this::createEnumDeserializer);
    }

    private JsonDeserializer<?> createEnumDeserializer(Class<?> enumClass) {
        EnumCodec<?> enumCodec = EnumCodec.of(enumClass);
        return new EasierEnumDeserializer<>(enumCodec);
    }
}
