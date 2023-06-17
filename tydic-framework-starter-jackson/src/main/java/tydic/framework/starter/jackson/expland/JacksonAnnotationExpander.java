package tydic.framework.starter.jackson.expland;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tydic.framework.core.plugin.jackson.expland.JsonExpand;
import tydic.framework.core.plugin.jackson.expland.JsonExpander;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class JacksonAnnotationExpander extends JacksonAnnotationIntrospector {

    @Override
    public Object findSerializer(Annotated annotated) {
        if (annotated instanceof AnnotatedMethod) {
            AnnotatedMethod annotatedMethod = (AnnotatedMethod) annotated;
            List<JsonFieldExpandDetail> details = StreamUtil.of(annotatedMethod.getAllAnnotations().annotations())
                    .map(this::mapToJsonFieldExpandDetail)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(details)) {
                return new JsonFieldExpandSerializer(annotated.getType(), details);
            }
        }
        return super.findSerializer(annotated);
    }

    @SneakyThrows
    private JsonFieldExpandDetail mapToJsonFieldExpandDetail(Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        JsonExpand jsonExpand = AnnotationUtil.getAnnotation(annotationType, JsonExpand.class);
        if (jsonExpand == null) {
            return null;
        }
        List<? extends JsonExpander<?>> expanders = Arrays.stream(jsonExpand.expandBy())
                .map(ReflectUtil::newInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(expanders)) {
            return null;
        }
        return JsonFieldExpandDetail.builder()
                .annotation(annotation)
                .expanders(expanders)
                .build();

    }
}