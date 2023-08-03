package easier.framework.core.plugin.dict;

import easier.framework.core.plugin.jackson.annotation.BoolReverse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

@Data
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class DictItemDetail implements Serializable {
    @Schema(description = "值")
    private String value;

    @Schema(description = "标签")
    private String label;

    @Schema(description = "顺序")
    private Integer sort;

    @Schema(description = "样式")
    private String style;

    @Schema(description = "是否启用")
    @BoolReverse("disable")
    private Boolean enable;

    @Schema(description = "备注")
    private String remark;
}
