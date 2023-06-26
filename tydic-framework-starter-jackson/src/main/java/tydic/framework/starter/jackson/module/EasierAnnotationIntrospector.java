package tydic.framework.starter.jackson.module;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.stream.StreamUtil;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.property.PropertyNamer;
import tydic.framework.core.plugin.jackson.annotation.Alias;
import tydic.framework.core.plugin.jackson.annotation.AliasId;
import tydic.framework.starter.jackson.expland.JsonFieldExpandDetail;
import tydic.framework.starter.jackson.expland.JsonFieldExpandSerializer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EasierAnnotationIntrospector extends AnnotationIntrospector {
    public static final EasierAnnotationIntrospector INSTANCE = new EasierAnnotationIntrospector();


    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public Object findSerializer(Annotated annotated) {
        if (annotated instanceof AnnotatedMethod) {
            AnnotatedMethod annotatedMethod = (AnnotatedMethod) annotated;
            Iterable<Annotation> annotations = annotatedMethod.getAllAnnotations().annotations();
            List<JsonFieldExpandDetail> details = StreamUtil.of(annotations)
                    .map(JsonFieldExpandDetail::from)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(details)) {
                return null;
            }
            return new JsonFieldExpandSerializer(annotated.getType(), details);
        }
        return null;
    }

    @Override
    public List<PropertyName> findPropertyAliases(Annotated annotated) {
        Alias alias = _findAnnotation(annotated, Alias.class);
        AliasId aliasId = _findAnnotation(annotated, AliasId.class);
        if (alias == null && aliasId == null) {
            return null;
        }
        String[] aliasValues = alias == null ? new String[]{AliasId.Expander.ID} : alias.value();
        boolean deserializeFirst = alias == null ? aliasId.deserializeFirst() : alias.deserializeFirst();
        List<PropertyName> result = Arrays.stream(aliasValues)
                .map(PropertyName::construct)
                .collect(Collectors.toList());
        String name = annotated.getName();
        String property = PropertyNamer.methodToProperty(name);
        if (deserializeFirst) {
            result.add(PropertyName.construct(property));
        } else {
            result.add(0, PropertyName.construct(property));
        }
        return result;
    }
}
