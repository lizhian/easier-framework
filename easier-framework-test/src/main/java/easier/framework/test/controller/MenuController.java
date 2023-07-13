package easier.framework.test.controller;

import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.tree.TreeNode;
import easier.framework.test.eo.Menu;
import easier.framework.test.qo.MenuTreeQo;
import easier.framework.test.service.DictService;
import easier.framework.test.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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


    @Operation(summary = "加载字典")
    @GetMapping("/menu/dict")
    public R<Map<String, DictDetail>> dict(CodesQo qo) {
        Map<String, DictDetail> map = this.dictService.loadDictDetail(qo);
        return R.success(map);
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


}
