package easier.framework.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.CodeQo;
import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.PageParam;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.test.eo.Role;
import easier.framework.test.eo.User;
import easier.framework.test.qo.RoleAssignAppQo;
import easier.framework.test.qo.RoleQo;
import easier.framework.test.service.DictService;
import easier.framework.test.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @Operation(summary = "分配应用")
    @PutMapping("/role/assignApp")
    public R<String> assignApp(@Validated @RequestBody RoleAssignAppQo qo) {
        this.roleService.assignApp(qo);
        return R.success();
    }


    /*@Operation(summary = "分配y")
    @PutMapping("/role/assignApp")
    public R<String> assignApp(@Validated @RequestBody RoleAssignAppQo qo) {
        this.roleService.assignApp(qo);
        return R.success();
    }*/


}
