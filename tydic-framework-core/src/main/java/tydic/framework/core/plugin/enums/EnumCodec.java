package tydic.framework.core.plugin.enums;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tydic.framework.core.util.InstanceUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 枚举编解码器
 */
@Slf4j
public class EnumCodec<E extends Enum<E>> {
    private final Map<E, String> enum2Value = MapUtil.newHashMap();
    private final Map<String, E> value2Enum = MapUtil.newHashMap();
    private final Map<String, E> label2Enum = MapUtil.newHashMap();
    private final Map<String, String> value2Label = MapUtil.newHashMap();
    private final Map<E, String> enum2Label = MapUtil.newHashMap();
    @Getter
    private final List<String> values = CollUtil.newArrayList();
    @Getter
    private final Class<E> enumClass;
    @Getter
    private boolean isInt;
    @Getter
    private String dictType;

    private EnumCodec(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.init();
    }

    /**
     * 构建方法
     */
    public static EnumCodec<?> of(Class<?> enumClass) {
        return InstanceUtil.in(EnumCodec.class)
                .getInstance(enumClass, () -> new EnumCodec(enumClass));
    }

    private void init() {
        Dict dict = AnnotationUtil.getAnnotation(this.enumClass, Dict.class);
        if (dict != null) {
            this.dictType = dict.type();
        }
        final ArrayList<Field> fields = CollUtil.newArrayList(this.enumClass.getDeclaredFields());
        final Field valueField = EnumCodecProvider.filterField(fields, EnumValue.class);
        final Field labelField = EnumCodecProvider.filterField(fields, EnumLabel.class);
        this.isInt = EnumCodecProvider.isInt(valueField);
        Enum<E>[] enums = this.enumClass.getEnumConstants();
        if (ArrayUtil.isEmpty(enums)) {
            return;
        }
        for (Enum<E> enumInstance : enums) {
            String enumValue = EnumCodecProvider.getFieldValue(enumInstance, valueField);
            String enumLabel = EnumCodecProvider.getFieldValue(enumInstance, labelField);
            this.values.add(enumValue);
            this.value2Enum.put(enumValue, (E) enumInstance);
            this.value2Label.put(enumValue, enumLabel);
            this.enum2Value.put((E) enumInstance, enumValue);
            this.enum2Label.put((E) enumInstance, enumLabel);
            this.label2Enum.put(enumLabel, (E) enumInstance);
        }
    }

    public Type getValueType() {
        return this.isInt ? Integer.class : String.class;
    }

    public Object enum2Value(E e) {
        return this.isInt ? this.enum2ValueAsInt(e) : this.enum2ValueAsString(e);

    }

    public String enum2ValueAsString(E e) {
        if (e == null) {
            return null;
        }
        return this.enum2Value.get(e);
    }

    public Integer enum2ValueAsInt(E e) {
        if (e == null) {
            return null;
        }
        return Integer.valueOf(this.enum2Value.get(e));
    }

    public String enum2Label(E e) {
        if (e == null) {
            return null;
        }
        return this.enum2Label.get(e);
    }

    public E value2Enum(Object value) {
        if (value == null) {
            return null;
        }
        return this.value2Enum.get(value.toString());
    }

    public E label2Enum(Object value) {
        if (value == null) {
            return null;
        }
        return this.label2Enum.get(value.toString());
    }

    public String value2Label(Object value) {
        if (value == null) {
            return null;
        }
        return this.value2Label.get(value.toString());
    }

    //从枚举/int/string转换成value;
    public Object any2Value(Object any) {
        if (any == null) {
            return null;
        }
        E[] enumConstants = this.enumClass.getEnumConstants();
        //枚举类型的判断
        for (E e : enumConstants) {
            if (e.equals(any)) {
                return this.enum2Value(e);
            }
        }
        //字符串类型的判断
        if (any instanceof String) {
            String string = (String) any;
            if (this.values.contains(string)) {
                return string;
            }
            //等于枚举名称
            for (E e : enumConstants) {
                if (e.name().equals(string)) {
                    return this.enum2Value(e);
                }
            }
        }
        //整数类型的判断
        if (any instanceof Integer) {
            Integer integer = (Integer) any;
            if (this.values.contains(integer.toString())) {
                return integer;
            }
        }
        //都不对
        return null;
    }

    public String getDescription() {
        String description = this.values
                .stream()
                .map(value -> value + "=" + this.value2Label.get(value))
                .collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(this.dictType)) {
            description = description + ",字典编码:【" + this.dictType + "】";
        }
        return description;
    }


    private static class EnumCodecProvider {

        public static Field filterField(ArrayList<Field> fields, Class<? extends Annotation> annotationClass) {
            return fields
                    .stream()
                    .filter(field -> AnnotationUtil.hasAnnotation(field, annotationClass))
                    .findAny()
                    .orElse(null);
        }

        public static boolean isInt(Field valueField) {
            if (valueField == null) {
                return false;
            }
            Class<?> valueFieldType = valueField.getType();
            return valueFieldType.equals(int.class) || valueFieldType.equals(Integer.class);
        }

        public static <E extends Enum<E>> String getFieldValue(Enum<E> enumInstance, Field field) {
            if (field == null) {
                return enumInstance.name();
            }
            Object fieldValue = ReflectUtil.getFieldValue(enumInstance, field.getName());
            return fieldValue.toString();
        }
    }
}