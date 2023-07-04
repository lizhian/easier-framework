package easier.framework.core.domain;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.util.WebUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

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
    @Schema(description = "查询总数", hidden = true)
    private boolean searchCount = true;

    @Builder.Default
    @Schema(description = "当前页")
    private long pageNo = 1;

    @Builder.Default
    @Schema(description = "每页大小")
    private long pageSize = 10;

    public static PageParam of(long pageNo, long pageSize) {
        return PageParam.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .searchCount(true)
                .build();
    }

    public static PageParam of(long pageNo, long pageSize, boolean searchCount) {
        return PageParam.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .searchCount(searchCount)
                .build();
    }

    public static PageParam fromRequest() {
        PageParam result = new PageParam();
        HttpServletRequest request = WebUtil.getRequest();
        if (request == null) {
            return result;
        }
        String pageNo = request.getParameter(Fields.pageNo);
        if (NumberUtil.isLong(pageNo)) {
            result.setPageNo(Long.parseLong(pageNo));
        }
        String pageSize = request.getParameter(Fields.pageSize);
        if (NumberUtil.isLong(pageSize)) {
            result.setPageSize(Long.parseLong(pageSize));
        }
        return result;
    }

    public <T> Page<T> toPage() {
        return new Page<>(this.pageNo, this.pageSize, this.searchCount);
    }

}

