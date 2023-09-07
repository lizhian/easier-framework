package easier.framework.core.plugin.dict;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tangzc.mpe.autotable.annotation.Column;
import easier.framework.core.plugin.jackson.annotation.BoolReverse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class DictDetail implements Serializable {
    @Schema(description = "当前值对应的字典项")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DictItemDetail selected;

    @Schema(description = "字典编码")
    private String code;

    @Schema(description = "字典名称")
    private String name;

    @Schema(description = "是否启用")
    @BoolReverse("disabled")
    private Boolean enable;

    @Column(comment = "样式")
    private String style;

    @Column(comment = "备注")
    private String remark;

    @Column(comment = "字典项集合")
    private List<DictItemDetail> items;
}
