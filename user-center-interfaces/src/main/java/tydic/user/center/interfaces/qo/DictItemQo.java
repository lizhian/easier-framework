package tydic.user.center.interfaces.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tydic.user.center.interfaces.enums.EnableStatus;

import javax.validation.constraints.NotBlank;

/**
 * 字典项目请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
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
