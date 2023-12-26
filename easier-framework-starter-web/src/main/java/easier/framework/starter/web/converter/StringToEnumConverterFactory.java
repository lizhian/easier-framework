package easier.framework.starter.web.converter;

import easier.framework.core.Easier;
import easier.framework.core.plugin.enums.EnumCodec;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

@SuppressWarnings({"rawtypes", "unchecked"})
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    /**
     * StringToEnumConverterFactory的单例实例
     */
    public final static StringToEnumConverterFactory instance = new StringToEnumConverterFactory();

    /**
     * 私有构造方法
     */
    private StringToEnumConverterFactory() {
    }

    /**
     * 获取针对指定目标类型的Converter
     *
     * @param targetType 目标类型的类对象
     * @param <T>        T extends Enum
     * @return 生成的Converter对象
     */
    @NotNull
    @Override
    public <T extends Enum> Converter<String, T> getConverter(@NotNull Class<T> targetType) {
        return new StringToEnumConverter<>(Easier.Enum.of(targetType));
    }

    /**
     * StringToEnumConverter类
     */
    @Data
    @RequiredArgsConstructor
    public static class StringToEnumConverter<T extends Enum<T>> implements Converter<String, T> {
        /**
         * 枚举码的实例
         */
        private final EnumCodec<T> codec;

        /**
         * 将字符串转换为枚举对象
         *
         * @param source 字符串对象
         * @return 转换后的枚举对象
         */
        @Override
        public T convert(@NotNull String source) {
            return this.codec.getEnumInstance(source);
        }

    }
}

