package easier.framework.starter.jackson.expland;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import easier.framework.core.plugin.jackson.expland.JsonExpand;
import easier.framework.core.plugin.jackson.expland.JsonExpander;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder(toBuilder = true)
public class JsonFieldExpandDetail {
    private Annotation annotation;
    private List<? extends JsonExpander<?>> expanders;

    public static JsonFieldExpandDetail from(Annotation annotation) {
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
