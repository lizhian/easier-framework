package easier.framework.test.controller;

import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.IdQo;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.tree.TreeNode;
import easier.framework.test.eo.App;
import easier.framework.test.eo.Menu;
import easier.framework.test.qo.AppQo;
import easier.framework.test.qo.MenuTreeQo;
import easier.framework.test.service.AppService;
import easier.framework.test.service.DictService;
import easier.framework.test.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 菜单管理
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Tag(name = "菜单管理")
@RestController
@RequiredArgsConstructor
public class MenuController {

    private final DictService dictService;
    private final MenuService menuService;
    private final AppService appService;


    @Operation(summary = "加载字典")
    @GetMapping("/menu/dict")
    public R<Map<String, DictDetail>> dict(@Validated CodesQo qo) {
        Map<String, DictDetail> map = this.dictService.dictDetail(qo.getCodes());
        return R.success(map);
    }

    @Operation(summary = "获取应用列表")
    @GetMapping("/menu/app/list")
    public R<List<App>> appList(AppQo qo) {
        List<App> list = this.appService.list(qo);
        return R.success(list);
    }

    @Operation(summary = "获取菜单树")
    @GetMapping("/menu/tree")
    public R<List<TreeNode<Menu>>> tree(MenuTreeQo qo) {
        List<TreeNode<Menu>> tree = this.menuService.tree(qo);
        return R.success(tree);
    }

    @Operation(summary = "添加菜单")
    @PostMapping("/menu/add")
    public R<String> add(@RequestBody Menu entity) {
        this.menuService.add(entity);
        return R.success();
    }

    @Operation(summary = "修改菜单")
    @PutMapping("/menu/update")
    public R<String> update(@RequestBody Menu entity) {
        this.menuService.update(entity);
        return R.success();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/menu/delete")
    public R<String> delete(@Validated IdQo qo) {
        this.menuService.delete(qo.getId());
        return R.success();
    }


}
