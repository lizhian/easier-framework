package easier.framework.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.PageParam;
import easier.framework.core.domain.R;
import easier.framework.test.eo.RunningLog;
import easier.framework.test.qo.RunningLogQo;
import easier.framework.test.service.RunningLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运行日志查询
 *
 * @author lizhian
 * @date 2023/07/05
 */
@Tag(name = "运行日志查询")
@RestController
@RequiredArgsConstructor
public class RunningLogController {
    private final RunningLogService runningLogService;

    @Operation(summary = "查询部门树")
    @GetMapping("/running/log/page")
    public R<Page<RunningLog>> page(PageParam pageParam, RunningLogQo qo) {
        Page<RunningLog> page = this.runningLogService.page(pageParam, qo);
        return R.success(page);
    }
}
