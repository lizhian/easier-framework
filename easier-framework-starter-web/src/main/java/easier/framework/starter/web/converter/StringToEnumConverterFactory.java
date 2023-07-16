package easier.framework.starter.web.converter;

import cn.hutool.core.util.StrUtil;
import easier.framework.core.plugin.enums.EnumCodec;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    public final static StringToEnumConverterFactory instance = new StringToEnumConverterFactory();

    private StringToEnumConverterFactory() {
    }

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter<>(EnumCodec.of(targetType));
    }

    @Data
    @RequiredArgsConstructor
    public static class StringToEnumConverter<T extends Enum<?>> implements Converter<String, T> {
        private final EnumCodec<?> enumCodec;

        @Override
        public T convert(String source) {
            if (StrUtil.isBlank(source)) {
                return null;
            }
            return (T) this.enumCodec.getEnumInstance(source);
        }

    }
}