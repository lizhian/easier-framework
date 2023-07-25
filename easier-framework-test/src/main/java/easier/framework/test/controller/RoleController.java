package easier.framework.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.CodeQo;
import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.PageParam;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.tree.TreeNode;
import easier.framework.test.eo.App;
import easier.framework.test.eo.Menu;
import easier.framework.test.eo.Role;
import easier.framework.test.eo.User;
import easier.framework.test.qo.*;
import easier.framework.test.service.AppService;
import easier.framework.test.service.DictService;
import easier.framework.test.service.MenuService;
import easier.framework.test.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;


/**
 * 角色管理
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Tag(name = "角色管理")
@RestController
@RequiredArgsConstructor
public class RoleController {

    private final DictService dictService;

    private final RoleService roleService;
    private final AppService appService;
    private final MenuService menuService;


    /**
     * 字典
     *
     * @param qo 请求对象
     * @return {@link R}<{@link Map}<{@link String}, {@link DictDetail}>>
     */
    @Operation(summary = "加载字典")
    @GetMapping("/role/dict")
    public R<Map<String, DictDetail>> dict(CodesQo qo) {
        Map<String, DictDetail> map = this.dictService.loadDictDetail(qo);
        return R.success(map);
    }


    /**
     * 分页角色
     *
     * @param qo 请求对象
     * @return {@link R}<{@link Page}<{@link User}>>
     */
    @Operation(summary = "查询角色-分页")
    @GetMapping("/role/page")
    public R<Page<Role>> pageRole(PageParam pageParam, RoleQo qo) {
        Page<Role> page = this.roleService.pageRole(pageParam, qo);
        return R.success(page);
    }


    /**
     * 添加角色
     *
     * @param entity 实体
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "添加角色")
    @PostMapping("/role/add")
    public R<String> addRole(@Validated @RequestBody Role entity) {
        this.roleService.addRole(entity);
        return R.success();
    }


    /**
     * 更新角色
     *
     * @param entity 实体
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "更新角色")
    @PutMapping("/role/update")
    public R<String> updateRole(@Validated @RequestBody Role entity) {
        this.roleService.updateRole(entity);
        return R.success();
    }


    /**
     * 删除角色
     *
     * @param qo 请求对象
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/role/delete")
    public R<String> deleteRole(@Validated CodeQo qo) {
        this.roleService.deleteRole(qo.getCode());
        return R.success();
    }


    @Operation(summary = "应用列表")
    @GetMapping("/role/app/list")
    public R<List<App>> appList(AppQo qo) {
        List<App> list = this.appService.list(qo);
        return R.success(list);
    }


    @Operation(summary = "分配应用")
    @PutMapping("/role/assignApp")
    public R<String> assignApp(@Validated @RequestBody RoleAssignAppQo qo) {
        this.roleService.assignApp(qo);
        return R.success();
    }


    @Operation(summary = "已分配角色的用户查询-分页")
    @GetMapping("/role/user/assigned/page")
    public R<Page<User>> assignedUserPage(@NotBlank String roleCode, UserQo qo, PageParam pageParam) {
        Page<User> page = this.roleService.assignedUserPage(roleCode, qo, pageParam);
        return R.success(page);
    }

    @Operation(summary = "未分配角色的用户查询-分页")
    @GetMapping("/role/user/unassigned/page")
    public R<Page<User>> unassignedUserPage(@NotBlank String roleCode, UserQo qo, PageParam pageParam) {
        Page<User> page = this.roleService.unassignedUserPage(roleCode, qo, pageParam);
        return R.success(page);
    }

    @Operation(summary = "分配用户")
    @PutMapping("/role/assignUser")
    public R<String> assignUser(@Validated @RequestBody RoleAssignUserQo qo) {
        this.roleService.assignUser(qo);
        return R.success();
    }

    @Operation(summary = "取消分配用户")
    @PutMapping("/role/unAssignUser")
    public R<String> unAssignUser(@Validated @RequestBody RoleAssignUserQo qo) {
        this.roleService.unAssignUser(qo);
        return R.success();
    }


    /*@Operation(summary = "获取角色关联的应用列表")
    @GetMapping("/role/app/listByRoleCode")
    public R<List<App>> appList(@Validated RoleCodeQo qo) {
        List<App> list = this.appService.listByRoleCode(qo.getRoleCode());
        return R.success(list);
    }*/

    @Operation(summary = "获取已选中的菜单列表")
    @GetMapping("/role/menu/selected")
    public R<List<String>> menuSelected(@Validated RoleMenuQo qo) {
        List<String> tree = this.roleService.menuSelected(qo);
        return R.success(tree);
    }

    @Operation(summary = "获取菜单列表")
    @GetMapping("/role/menu/tree")
    public R<List<TreeNode<Menu>>> menuList(@Validated MenuTreeQo qo) {
        List<TreeNode<Menu>> tree = this.menuService.tree(qo);
        return R.success(tree);
    }


    @Operation(summary = "角色分配菜单")
    @PutMapping("/role/assignMenu")
    public R<String> assignMenu(@Validated RoleMenuQo qo) {
        this.roleService.assignMenu(qo);
        return R.success();
    }


}
