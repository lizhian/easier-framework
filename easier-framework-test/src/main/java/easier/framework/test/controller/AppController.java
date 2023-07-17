package easier.framework.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.CodeQo;
import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.PageParam;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.test.eo.App;
import easier.framework.test.qo.AppAssignRoleQo;
import easier.framework.test.qo.AppQo;
import easier.framework.test.service.AppService;
import easier.framework.test.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 应用管理
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Tag(name = "应用管理")
@RestController
@RequiredArgsConstructor
public class AppController {

    private final DictService dictService;

    private final AppService appService;

    @Operation(summary = "加载字典")
    @GetMapping("/app/dict")
    public R<Map<String, DictDetail>> dict(@Validated CodesQo qo) {
        Map<String, DictDetail> map = this.dictService.loadDictDetail(qo);
        return R.success(map);
    }

    @Operation(summary = "查询应用-分页")
    @GetMapping("/app/page")
    public R<Page<App>> pageApp(PageParam pageParam, AppQo qo) {
        Page<App> page = this.appService.pageApp(pageParam, qo);
        return R.success(page);
    }


    @Operation(summary = "添加应用")
    @PostMapping("/app/add")
    public R<String> addApp(@Validated @RequestBody App entity) {
        this.appService.addApp(entity);
        return R.success();
    }


    @Operation(summary = "更新应用")
    @PutMapping("/app/update")
    public R<String> updateApp(@Validated @RequestBody App entity) {
        this.appService.updateApp(entity);
        return R.success();
    }


    @Operation(summary = "删除应用")
    @DeleteMapping("/app/delete")
    public R<String> deleteApp(@Validated CodeQo qo) {
        this.appService.deleteApp(qo.getCode());
        return R.success();
    }


    @Operation(summary = "分配角色")
    @PutMapping("/app/assignRole")
    public R<String> assignRole(@Validated @RequestBody AppAssignRoleQo qo) {
        this.appService.assignRole(qo);
        return R.success();
    }
}
