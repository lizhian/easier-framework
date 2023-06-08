package tydic.framework.core.domain;

import cn.hutool.core.collection.CollUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import tydic.framework.core.plugin.exception.biz.BizException;
import tydic.framework.core.util.StrUtil;

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

    @ApiModelProperty("数据主键")
    private String id;

    @ApiModelProperty("数据主键集合")
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

