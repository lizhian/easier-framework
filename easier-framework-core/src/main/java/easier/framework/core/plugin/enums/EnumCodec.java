package easier.framework.core.plugin.enums;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import easier.framework.core.plugin.dict.Dict;
import easier.framework.core.util.InstanceUtil;
import easier.framework.core.util.ListUtil;
import easier.framework.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 枚举编解码器
 */
@Slf4j
public class EnumCodec<E extends Enum<E>> {

    @Getter
    private final Class<E> enumClass;
    @Getter
    private final boolean isIntValue;
    @Getter
    private final Dict dict;
    @Getter
    private final List<EnumDetail<E>> enumDetails;

    /**
     * 构建方法
     */
    public static EnumCodec<?> of(Class<?> enumClass) {
        return InstanceUtil.in(EnumCodec.class)
                .getInstance(enumClass, () -> new EnumCodec(enumClass));
    }

    private EnumCodec(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.dict = AnnotationUtil.getAnnotation(this.enumClass, Dict.class);
        ArrayList<Field> fields = CollUtil.newArrayList(this.enumClass.getDeclaredFields());
        Field valueField = EnumCodecProvider.filterField(fields, EnumValue.class);
        Field descField = EnumCodecProvider.filterField(fields, EnumDesc.class);
        this.isIntValue = EnumCodecProvider.isIntValue(valueField);
        Enum<E>[] instances = this.enumClass.getEnumConstants();
        if (instances == null) {
            this.enumDetails = ListUtil.unmodifiable(new ArrayList<>());
            return;
        }
        List<EnumDetail<E>> enumDetails = new ArrayList<>();
        for (int index = 0; index < instances.length; index++) {
            E instance = enumClass.getEnumConstants()[index];
            Object value = EnumCodecProvider.getFieldValue(instance, valueField);
            Object description = EnumCodecProvider.getFieldValue(instance, descField);
            EnumDetail<E> enumDetail = EnumDetail.<E>builder()
                    .index(index)
                    .instance(instance)
                    .value(value)
                    .isIntValue(isIntValue)
                    .description(description.toString())
                    .dictCode(dict == null ? null : dict.code())
                    .dictName(dict == null ? null : dict.name())
                    .dictProperty1(dict == null ? null : dict.property1())
                    .dictProperty2(dict == null ? null : dict.property2())
                    .dictProperty3(dict == null ? null : dict.property3())
                    .build();
            enumDetails.add(enumDetail);
        }
        this.enumDetails = ListUtil.unmodifiable(enumDetails);
    }

    public Type getValueType() {
        return this.isIntValue ? Integer.class : String.class;
    }

    /**
     * 获取枚举详情
     * 枚举实例->枚举值->枚举名称->枚举下标
     */
    public EnumDetail<E> getEnumDetail(Object input) {
        if (input == null) {
            return null;
        }
        EnumDetail<E> enumDetail = enumDetails.stream()
                .filter(it -> it.getInstance().equals(input))
                .findAny()
                .orElse(null);
        if (enumDetail != null) {
            return enumDetail;
        }
        enumDetail = enumDetails.stream()
                .filter(it -> it.getValue().equals(input))
                .findAny()
                .orElse(null);
        if (enumDetail != null) {
            return enumDetail;
        }
        enumDetail = enumDetails.stream()
                .filter(it -> it.getInstance().name().equals(input))
                .findAny()
                .orElse(null);
        if (enumDetail != null) {
            return enumDetail;
        }
        enumDetail = enumDetails.stream()
                .filter(it -> it.getIndex().toString().equals(input.toString()))
                .findAny()
                .orElse(null);
        return enumDetail;
    }

    /**
     * 获取枚举实例
     */
    public E getEnumInstance(Object input) {
        EnumDetail<E> enumDetail = getEnumDetail(input);
        if (enumDetail == null) {
            return null;
        }
        return enumDetail.getInstance();
    }

    /**
     * 获取枚举值
     */
    public Object getEnumValue(Object input) {
        EnumDetail<E> enumDetail = getEnumDetail(input);
        if (enumDetail == null) {
            return null;
        }
        return enumDetail.getValue();
    }

    /**
     * 获取枚举描述
     */
    @NotNull
    public String getEnumDesc(Object input) {
        EnumDetail<E> enumDetail = getEnumDetail(input);
        if (enumDetail == null) {
            return "";
        }
        return enumDetail.getDescription();
    }

    public Integer getEnumValueAsInt(Object input) {
        EnumDetail<E> enumDetail = getEnumDetail(input);
        if (enumDetail != null) {
            return enumDetail.getValueAsInt();
        }
        return null;
    }

    public String getEnumValueAsStr(Object input) {
        EnumDetail<E> enumDetail = getEnumDetail(input);

        if (enumDetail != null) {
            return enumDetail.getValueAsStr();
        }
        return "";
    }

    public String getEnumFullDescription() {
        String str = enumDetails.stream()
                .map(it -> it.getValue() + "=" + it.getDescription())
                .collect(Collectors.joining(", "));
        if (dict == null) {
            return StrUtil.format("枚举值说明:【{}】", str);
        }
        return StrUtil.format("枚举字典【{}】【{}】【{}】"
                , dict.name()
                , dict.code()
                , str
        );
    }


    private static class EnumCodecProvider {

        public static Field filterField(ArrayList<Field> fields, Class<? extends Annotation> annotationClass) {
            return fields
                    .stream()
                    .filter(field -> AnnotationUtil.hasAnnotation(field, annotationClass))
                    .findAny()
                    .orElse(null);
        }

        public static boolean isIntValue(Field valueField) {
            if (valueField == null) {
                return false;
            }
            Class<?> valueFieldType = valueField.getType();
            return valueFieldType.equals(int.class) || valueFieldType.equals(Integer.class);
        }

        @Nonnull
        public static <E extends Enum<E>> Object getFieldValue(Enum<E> enumInstance, Field field) {
            if (field == null) {
                return enumInstance.name();
            }
            return ReflectUtil.getFieldValue(enumInstance, field.getName());
        }
    }
}