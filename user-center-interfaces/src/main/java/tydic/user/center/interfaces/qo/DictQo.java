package tydic.user.center.interfaces.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tydic.user.center.interfaces.enums.DictType;
import tydic.user.center.interfaces.enums.EnableStatus;

/**
 * 字典请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
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
