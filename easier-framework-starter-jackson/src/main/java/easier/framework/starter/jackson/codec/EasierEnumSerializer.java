package easier.framework.starter.jackson.codec;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import easier.framework.core.plugin.enums.EnumCodec;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class EasierEnumSerializer<E extends Enum<E>> extends JsonSerializer<E> {
    private final EnumCodec<E> enumCodec;

    @Override
    public void serialize(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (this.enumCodec.isIntValue()) {
            gen.writeNumber(this.enumCodec.getEnumValueAsInt(value));
        } else {
            gen.writeString(this.enumCodec.getEnumValueAsStr(value));
        }
    }
}