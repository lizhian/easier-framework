package tydic.framework.starter.jackson.expland;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import tydic.framework.core.plugin.jackson.expland.JsonExpander;

import java.lang.annotation.Annotation;
import java.util.List;

@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder(toBuilder = true)
public class JsonFieldExpandDetail {
    private Annotation annotation;
    private List<? extends JsonExpander<?>> expanders;
}
