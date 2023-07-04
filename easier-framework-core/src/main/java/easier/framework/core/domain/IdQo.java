package easier.framework.core.domain;

import cn.hutool.core.util.NumberUtil;
import easier.framework.core.plugin.exception.biz.BizException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 数据主键参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class IdQo implements Serializable {

    @Schema(description = "数据主键")
    @NotBlank
    private String id;


    public int asIntId() {
        if (!NumberUtil.isInteger(id)) {
            throw BizException.of("数据主键无法转成Integer类型:{}", id);
        }
        return NumberUtil.parseInt(id);
    }

    public long asLongId() {
        if (!NumberUtil.isLong(id)) {
            throw BizException.of("数据主键无法转成Long类型:{}", id);
        }
        return NumberUtil.parseLong(id);
    }

}

