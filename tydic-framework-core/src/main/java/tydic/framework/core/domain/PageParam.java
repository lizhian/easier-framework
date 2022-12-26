package tydic.framework.core.domain;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import tydic.framework.core.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 分页参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class PageParam implements Serializable {

    @Builder.Default
    @ApiModelProperty(value = "查询总数", hidden = true)
    private boolean searchCount = true;

    @Builder.Default
    @ApiModelProperty("当前页")
    private long current = 1;

    @Builder.Default
    @ApiModelProperty("每页大小")
    private long size = 10;

    public <T> Page<T> toPage() {
        return new Page<>(this.current, this.size, this.searchCount);
    }

    public static PageParam of(long current, long size) {
        return PageParam.builder()
                        .current(current)
                        .size(size)
                        .searchCount(true)
                        .build();
    }

    public static PageParam of(long current, long size, boolean searchCount) {
        return PageParam.builder()
                        .current(current)
                        .size(size)
                        .searchCount(searchCount)
                        .build();
    }

    public static PageParam fromRequest() {
        PageParam result = new PageParam();
        HttpServletRequest request = WebUtil.getRequest();
        if (request == null) {
            return result;
        }
        String current = request.getParameter(Fields.current);
        if (NumberUtil.isLong(current)) {
            result.setCurrent(Long.parseLong(current));
        }
        String size = request.getParameter(Fields.size);
        if (NumberUtil.isLong(size)) {
            result.setSize(Long.parseLong(size));
        }
        return result;
    }

}

