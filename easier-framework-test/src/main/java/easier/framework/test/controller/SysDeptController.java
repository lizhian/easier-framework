package easier.framework.test.controller;

import easier.framework.core.domain.R;
import easier.framework.core.domain.TreeNode;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.eo.SysDept;
import easier.framework.test.qo.DeptQo;
import easier.framework.test.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门信息
 *
 * @author ruoyi
 */
@Tag(name = "部门管理")
@RestController
@RequiredArgsConstructor
public class SysDeptController {
    private final Repo<SysDept> deptRepo = Repos.of(SysDept.class);
    private final SysDeptService deptService;

    @Operation(summary = "获取部门列表")
    //@MustPermission("system:dept:list")
    @GetMapping("/system/dept/list")
    public R<List<SysDept>> list(DeptQo deptQo) {
        return R.success(deptService.selectDeptList(deptQo));
    }

    @Operation(summary = "获取部门列表,树结构")
    //@MustPermission("system:dept:list")
    @GetMapping("/system/dept/listTree")
    public R<List<TreeNode<SysDept>>> selectDeptTree(DeptQo deptQo) {
        return R.success(deptService.selectDeptTree(deptQo));
    }

    @Operation(summary = "查询部门列表（排除节点）")
    //@MustPermission("system:dept:list")
    @GetMapping("/system/dept/list/exclude/{deptId}")
    public R<List<SysDept>> excludeChild(@PathVariable(value = "deptId", required = false) String deptId) {
        return R.success(deptService.excludeChild(deptId));
    }

    @Operation(summary = "根据部门编号获取详细信息")
    //@MustPermission("system:dept:query")
    @GetMapping(value = "/system/dept/{deptId}")
    public R<SysDept> getInfo(@PathVariable String deptId) {
        SysDept dept = deptRepo.withBind().getById(deptId);
        return R.success(dept);
    }

    @Operation(summary = "新增部门")
    //@MustPermission("system:dept:add")
    @PostMapping("/system/dept")
    public R<Boolean> add(@Validated @RequestBody SysDept dept) {
        deptService.add(dept);
        return R.success();
    }

    @Operation(summary = "修改部门")
    //@MustPermission("system:dept:edit")
    @PutMapping("/system/dept")
    public R<Boolean> edit(@RequestBody SysDept dept) {
        deptService.update(dept);
        return R.success();
    }


    @Operation(summary = "删除部门")
    //@MustPermission("system:dept:remove")
    @DeleteMapping("/system/dept/{deptId}")
    public R<Boolean> remove(@PathVariable String deptId) {
        deptService.deleteDeptById(deptId);
        return R.success();
    }
}
