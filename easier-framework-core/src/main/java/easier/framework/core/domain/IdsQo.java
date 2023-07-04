package easier.framework.core.domain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据主键参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class IdsQo implements Serializable {
    @Schema(description = "数据主键集合")
    @NotEmpty
    private List<String> ids;

    public List<Integer> asIntId() {
        return CollUtil.newArrayList(ids)
                .stream()
                .filter(StrUtil::isNotBlank)
                .map(id -> {
                    if (!NumberUtil.isInteger(id)) {
                        throw BizException.of("数据主键无法转成Integer类型:{}", id);
                    }
                    return NumberUtil.parseInt(id);
                })
                .collect(Collectors.toList());
    }

    public List<Long> asLongId() {
        return CollUtil.newArrayList(ids)
                .stream()
                .filter(StrUtil::isNotBlank)
                .map(id -> {
                    if (!NumberUtil.isLong(id)) {
                        throw BizException.of("数据主键无法转成Long类型:{}", id);
                    }
                    return NumberUtil.parseLong(id);
                })
                .collect(Collectors.toList());
    }
}

