package easier.framework.starter.doc.customizer;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.type.SimpleType;
import easier.framework.core.plugin.enums.Dict;
import easier.framework.core.plugin.enums.EnumCodec;
import easier.framework.core.plugin.enums.EnumDetail;
import easier.framework.core.plugin.mybatis.TableCode;
import easier.framework.core.util.ApiDocUtil;
import easier.framework.core.util.StrUtil;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EasierPropertyParameterCustomizer implements PropertyCustomizer, ParameterCustomizer {
    @Override
    public Schema customize(Schema property, AnnotatedType annotatedType) {
        if (property == null || annotatedType == null) {
            return property;
        }
        Annotation[] annotations = annotatedType.getCtxAnnotations();
        Class<?> clazz = null;
        if (annotatedType.getType() instanceof SimpleType) {
            SimpleType simpleType = (SimpleType) annotatedType.getType();
            clazz = simpleType.getRawClass();
        }
        List<String> descriptions = CollUtil.newArrayList();
        if (StrUtil.isNotBlank(property.getDescription())) {
            descriptions.add(property.getDescription());
        }
        List<String> _enum = new ArrayList<>();
        forDescription(annotations, descriptions);
        forEnumClass(clazz, descriptions, _enum);
        forDict(annotations, descriptions);
        forTableId(annotations, descriptions);
        forTableCode(annotations, descriptions);
        property.description(StrUtil.join(descriptions));
        property._enum(_enum.isEmpty() ? null : _enum);
        return property;
    }


    @Override
    public Parameter customize(Parameter parameter, MethodParameter methodParameter) {
        if (parameter == null || methodParameter == null) {
            return parameter;
        }
        Annotation[] annotations = methodParameter.getParameterAnnotations();
        Class<?> clazz = methodParameter.getParameterType();
        List<String> _enum = new ArrayList<>();
        List<String> descriptions = CollUtil.newArrayList();
        if (StrUtil.isNotBlank(parameter.getDescription())) {
            descriptions.add(parameter.getDescription());
        }
        forDescription(annotations, descriptions);
        forEnumClass(clazz, descriptions, _enum);
        forDict(annotations, descriptions);
        forTableId(annotations, descriptions);
        forTableCode(annotations, descriptions);
        Schema schema = parameter.getSchema();
        if (schema != null && !_enum.isEmpty()) {
            schema._enum(_enum);
        }
        parameter.description(StrUtil.join(descriptions));
        return parameter;
    }


    private void forDescription(Annotation[] annotations, List<String> descriptions) {
        if (CollUtil.isNotEmpty(descriptions)) {
            return;
        }
        if (annotations == null) {
            return;
        }
        String description = ApiDocUtil.getDescription(annotations);
        if (StrUtil.isNotBlank(description)) {
            descriptions.add(description);
        }
    }

    private void forEnumClass(Class<?> clazz, List<String> descriptions, List<String> _enum) {
        if (clazz == null) {
            return;
        }
        if (!clazz.isEnum()) {
            return;
        }
        EnumCodec<?> enumCodec = EnumCodec.of(clazz);
        descriptions.add(enumCodec.getEnumFullDescription());
        List<String> values = enumCodec.getEnumDetails()
                .stream()
                .map(EnumDetail::getValueAsStr)
                .collect(Collectors.toList());
        _enum.clear();
        _enum.addAll(values);
    }

    private void forDict(Annotation[] annotations, List<String> descriptions) {
        if (annotations == null) {
            return;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof Dict) {
                Dict dict = (Dict) annotation;
                String type = dict.type();
                String description = StrUtil.format("字典编码:【{}】", type);
                descriptions.add(description);
                return;
            }
        }
    }

    private void forTableId(Annotation[] annotations, List<String> descriptions) {
        if (annotations == null) {
            return;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof TableId) {
                descriptions.add("【主键】");
                return;
            }
        }
    }

    private void forTableCode(Annotation[] annotations, List<String> descriptions) {
        if (annotations == null) {
            return;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof TableCode) {
                descriptions.add("【唯一编码】");
                return;
            }
        }
    }


}
