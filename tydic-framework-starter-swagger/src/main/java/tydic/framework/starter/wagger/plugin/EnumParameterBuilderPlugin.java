package tydic.framework.starter.wagger.plugin;

import cn.hutool.core.util.ClassUtil;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import tydic.framework.core.plugin.enums.EnumCodec;

import java.util.Optional;

@Order
@RequiredArgsConstructor
public class EnumParameterBuilderPlugin implements ParameterBuilderPlugin {
    private final TypeResolver typeResolver;

    @Override
    public boolean supports(DocumentationType documentationType) {
        return SwaggerPluginSupport.pluginDoesApply(documentationType);
    }

    @Override
    public void apply(ParameterContext context) {
        Class<?> clazz = Optional.ofNullable(context)
                                 .map(ParameterContext::resolvedMethodParameter)
                                 .map(ResolvedMethodParameter::getParameterType)
                                 .map(ResolvedType::getErasedType)
                                 .orElse(null);
        if (!ClassUtil.isEnum(clazz)) {
            return;
        }
        EnumCodec<?> enumCodec = EnumCodec.of(clazz);
        String description = context.parameterBuilder().build().getDescription();
        context.parameterBuilder()
               .allowableValues(new AllowableListValues(enumCodec.getValues(), clazz.getTypeName()))
               .description(description + "," + enumCodec.getDescription())
               .type(this.typeResolver.resolve(enumCodec.getValueType()));
    }
}
