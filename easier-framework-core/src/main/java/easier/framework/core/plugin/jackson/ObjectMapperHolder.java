package easier.framework.core.plugin.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.cache.support.NullValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class ObjectMapperHolder implements ApplicationContextAware {
    @Setter(AccessLevel.PRIVATE)
    private volatile static ObjectMapper objectMapper;
    @Setter(AccessLevel.PRIVATE)
    private volatile static ObjectMapper typedObjectMapper;

    static {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        ObjectMapper init = builder.createXmlMapper(false).build();
        setObjectMapper(init);
        copyAndTyped();
    }

    private static void copyAndTyped() {
        typedObjectMapper = objectMapper.copy();
        typedObjectMapper.registerModule(new SimpleModule().addSerializer(new NullValueSerializer(null)));
        typedObjectMapper.activateDefaultTyping(typedObjectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }


    public static ObjectMapper get(boolean autoType) {
        return autoType ? typedObjectMapper : objectMapper;
    }

    public static ObjectMapper get() {
        return objectMapper;
    }

    public static ObjectMapper getTyped() {
        return typedObjectMapper;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setObjectMapper(applicationContext.getBean(ObjectMapper.class));
        copyAndTyped();
    }


    static class NullValueSerializer extends StdSerializer<NullValue> {
        private final String classIdentifier;

        public NullValueSerializer(@Nullable String classIdentifier) {
            super(NullValue.class);
            this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
        }

        @Override
        public void serialize(NullValue value, JsonGenerator jsonGenerator, SerializerProvider provider)
                throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(this.classIdentifier, NullValue.class.getName());
            jsonGenerator.writeEndObject();
        }
    }
}
