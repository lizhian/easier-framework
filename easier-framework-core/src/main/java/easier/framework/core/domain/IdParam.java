package easier.framework.core.domain;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
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
public class IdParam implements Serializable {

    @Schema(description = "数据主键")
    private String id;

    @Schema(description = "数据主键集合")
    private List<String> ids;

    public void valid() {
        if (StrUtil.isBlank(this.id) && CollUtil.isEmpty(this.ids)) {
            throw BizException.of("数据主键不能为空");
        }
    }


    @Nonnull
    public List<String> toList() {
        List<String> result = new ArrayList<>();
        result.add(this.id);
        result.addAll(this.ids);
        return result.stream()
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

}

