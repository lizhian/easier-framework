package easier.framework.starter.jackson.module;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import easier.framework.starter.jackson.codec.EasierDateDeserializer;
import easier.framework.starter.jackson.codec.EasierDateTimeDeserializer;
import easier.framework.starter.jackson.codec.EasierDateTimeSerializer;

import java.util.Date;

public class EasierJacksonModule extends Module {
    @Override
    public String getModuleName() {
        return EasierJacksonModule.class.getSimpleName();
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(serializers());
        context.addDeserializers(deserializers());
        context.addBeanSerializerModifier(EasierBeanSerializerModifier.INSTANCE);
        context.addBeanDeserializerModifier(EasierBeanDeserializerModifier.INSTANCE);
        context.insertAnnotationIntrospector(EasierAnnotationIntrospector.INSTANCE);
    }


    private Serializers serializers() {
        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(Long.class, ToStringSerializer.instance);
        serializers.addSerializer(long.class, ToStringSerializer.instance);
        serializers.addSerializer(DateTime.class, EasierDateTimeSerializer.instance);
        return serializers;
    }

    private Deserializers deserializers() {
        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Date.class, EasierDateDeserializer.instance);
        deserializers.addDeserializer(DateTime.class, EasierDateTimeDeserializer.instance);
        return deserializers;
    }
}
