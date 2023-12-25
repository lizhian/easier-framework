package easier.framework.starter.web.converter;

import easier.framework.core.plugin.enums.EnumCodec;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

@SuppressWarnings({"rawtypes", "unchecked"})
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    public final static StringToEnumConverterFactory instance = new StringToEnumConverterFactory();

    private StringToEnumConverterFactory() {
    }

    @NotNull
    @Override
    public <T extends Enum> Converter<String, T> getConverter(@NotNull Class<T> targetType) {
        return new StringToEnumConverter<>(EnumCodec.of(targetType));
    }

    @Data
    @RequiredArgsConstructor
    public static class StringToEnumConverter<T extends Enum<?>> implements Converter<String, T> {
        private final EnumCodec<?> codec;

        @Override
        public T convert(@NotNull String source) {
            return (T) this.codec.getEnumInstance(source);
        }

    }
}
