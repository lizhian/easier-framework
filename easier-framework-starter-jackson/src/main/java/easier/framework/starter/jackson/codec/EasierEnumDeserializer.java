package easier.framework.starter.jackson.codec;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import easier.framework.core.plugin.enums.EnumCodec;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class EasierEnumDeserializer<E extends Enum<E>> extends JsonDeserializer<E> {
    private final EnumCodec<E> enumCodec;

    @Override
    public E deserialize(JsonParser p, DeserializationContext context) throws IOException {
        String value = p.getValueAsString();
        return this.enumCodec.getEnumInstance(value);
    }


}