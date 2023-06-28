package easier.framework.starter.wagger.plugin;

import com.fasterxml.classmate.TypeResolver;
import easier.framework.core.plugin.enums.EnumCodec;
import easier.framework.core.plugin.enums.EnumDetail;
import easier.framework.core.util.ClassUtil;
import easier.framework.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.List;
import java.util.stream.Collectors;

@Order
@Slf4j
@RequiredArgsConstructor
public class EnumExpandedParameterBuilderPlugin implements ExpandedParameterBuilderPlugin {
    private final TypeResolver typeResolver;


    @Override
    public boolean supports(DocumentationType documentationType) {
        return SwaggerPluginSupport.pluginDoesApply(documentationType);

    }

    @Override
    public void apply(ParameterExpansionContext context) {
        Class<?> clazz = context.getFieldType().getErasedType();
        if (!ClassUtil.isEnum(clazz)) {
            return;
        }
        EnumCodec<?> enumCodec = EnumCodec.of(clazz);
        String description = context.getParameterBuilder().build().getDescription();
        if (StrUtil.isBlank(description)) {
            description = context.getFieldName();
        }
        List<String> values = enumCodec.getEnumDetails()
                .stream()
                .map(EnumDetail::getValueAsStr)
                .collect(Collectors.toList());
        context.getParameterBuilder()
                .allowableValues(new AllowableListValues(values, clazz.getTypeName()))
                .description(description + "," + enumCodec.getEnumFullDescription())
                .type(this.typeResolver.resolve(enumCodec.getValueType()));
    }
}
