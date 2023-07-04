package easier.framework.test.qo;

import easier.framework.test.enums.DictType;
import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DictQo {

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "字典类型")
    private DictType dictType;

    @Schema(description = "字典状态")
    private EnableStatus status;
}
