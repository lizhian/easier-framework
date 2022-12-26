package tydic.framework.starter.wagger.plugin;

import cn.hutool.core.util.ClassUtil;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import tydic.framework.core.plugin.enums.EnumCodec;

@Order
@Slf4j
@RequiredArgsConstructor
public class EnumModelPropertyPlugin implements ModelPropertyBuilderPlugin {


    private final TypeResolver typeResolver;

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

    @Override
    public void apply(ModelPropertyContext context) {
        Class<?> clazz = context.getBeanPropertyDefinition()
                                .map(BeanPropertyDefinition::getRawPrimaryType)
                                .orElse(null);
        if (!ClassUtil.isEnum(clazz)) {
            return;
        }
        EnumCodec<?> enumCodec = EnumCodec.of(clazz);
        String description = context.getBuilder().build().getDescription();
        context.getBuilder()
               .allowableValues(new AllowableListValues(enumCodec.getValues(), clazz.getTypeName()))
               .description(description + "," + enumCodec.getDescription())
               .type(this.typeResolver.resolve(enumCodec.getValueType()));
    }
}


