package easier.framework.core.util;

import cn.hutool.core.collection.CollUtil;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.Function;

public class AnnotationUtil extends cn.hutool.core.annotation.AnnotationUtil {


    /**
     * 获取第一个可用的值
     *
     * @param mapping     映射函数，用于将注解转换为值
     * @param annotations 注解数组
     * @param <T>         注解类型
     * @param <V>         值类型
     * @return 第一个可用的值
     */
    @SafeVarargs
    public static <T extends Annotation, V> V getFirstAvailableValue(Function<T, V> mapping, T... annotations) {
        return getFirstAvailableValue(null, mapping, annotations); // 如果无可用值则返回默认值
    }

    /**
     * 获取第一个可用的值
     *
     * @param mapping      映射函数，用于将注解转换为值
     * @param defaultValue 默认值
     * @param annotations  注解列表
     * @param <T>          注解类型
     * @param <V>          值类型
     * @return 第一个可用的值，如果无可用值则返回默认值
     */
    @SafeVarargs
    public static <T extends Annotation, V> V getFirstAvailableValue(V defaultValue, Function<T, V> mapping, T... annotations) {
        return CollUtil.newArrayList(annotations)
                .stream() // 转换为流
                .filter(Objects::nonNull) // 过滤空值
                .map(mapping) // 映射函数转换注解为值
                .filter(StrUtil::isNotBlankIfStr) // 过滤空字符串
                .findFirst() // 获取第一个可用的值
                .orElse(defaultValue); // 如果无可用值则返回默认值
    }


}
