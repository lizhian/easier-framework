package tydic.framework.core.util;


import cn.hutool.core.util.ArrayUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import tydic.framework.core.plugin.jackson.ObjectMapperHolder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class JacksonUtil {

    @SneakyThrows
    public <T> T toBean(String json, Class<T> clazz) {
        ObjectMapper objectMapper = clazz.equals(Object.class) ?
                ObjectMapperHolder.withTyping() : ObjectMapperHolder.get();
        return objectMapper.readValue(json, clazz);
    }

    @SneakyThrows
    public <T> T toBean(byte[] bytes, Class<T> clazz) {
        ObjectMapper objectMapper = clazz.equals(Object.class) ?
                ObjectMapperHolder.withTyping() : ObjectMapperHolder.get();
        return objectMapper.readValue(bytes, clazz);
    }

    @SneakyThrows
    public Object toObject(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        return ObjectMapperHolder.withTyping().readValue(json, Object.class);
    }

    @SneakyThrows
    public Object toObject(byte[] bytes) {
        if (ArrayUtil.isEmpty(bytes)) {
            return null;
        }
        return ObjectMapperHolder.withTyping().readValue(bytes, Object.class);
    }

    @SneakyThrows
    public <T> List<T> toList(String json, Class<T> clazz) {
        ObjectMapper objectMapper = clazz.equals(Object.class) ?
                ObjectMapperHolder.withTyping() : ObjectMapperHolder.get();
        JavaType listType = collectionType(List.class, clazz);
        return objectMapper.readValue(json, listType);
    }

    @SneakyThrows
    public <T> Set<T> toSet(String json, Class<T> clazz) {
        ObjectMapper objectMapper = clazz.equals(Object.class) ?
                ObjectMapperHolder.withTyping() : ObjectMapperHolder.get();
        JavaType setType = collectionType(Set.class, clazz);
        return objectMapper.readValue(json, setType);
    }

    @SneakyThrows
    public static byte[] toBytes(Object value) {
        return ObjectMapperHolder.get().writeValueAsBytes(value);
    }

    @SneakyThrows
    public static byte[] toTypingBytes(Object value) {
        return ObjectMapperHolder.withTyping().writeValueAsBytes(value);
    }

    @SneakyThrows
    public static String toString(Object value) {
        return ObjectMapperHolder.get().writeValueAsString(value);
    }

    @SneakyThrows
    public static String toTypingString(Object value) {
        return ObjectMapperHolder.withTyping().writeValueAsString(value);
    }


    private static JavaType collectionType(Class<?> collectionClazz, Class<?> elementClazz) {
        return ObjectMapperHolder.get().getTypeFactory()
                                 .constructParametricType(collectionClazz, elementClazz);
    }

    @SneakyThrows
    public static Map<String, Object> toMapObject(String json) {
        return ObjectMapperHolder.get().readValue(json, new TypeReference<Map<String, Object>>() {
        });
    }
}
