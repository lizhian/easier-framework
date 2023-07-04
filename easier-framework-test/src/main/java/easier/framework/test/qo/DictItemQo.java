package easier.framework.test.qo;

import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DictItemQo {

    @Schema(description = "字典编码")
    @NotBlank
    private String dictCode;

    @Schema(description = "字典项标签")
    private String label;

    @Schema(description = "字典项键值")
    private String value;

    @Schema(description = "状态")
    private EnableStatus status;
}
