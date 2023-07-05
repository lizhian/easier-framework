package easier.framework.test.controller;

import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.tree.TreeNode;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.eo.SysDept;
import easier.framework.test.qo.DeptQo;
import easier.framework.test.service.DeptService;
import easier.framework.test.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *
 * @author lizhian
 * @date 2023/07/05
 */
@Tag(name = "部门管理")
@RestController
@RequiredArgsConstructor
public class DeptController {
    private final Repo<SysDept> _sys_dept = Repos.of(SysDept.class);
    private final DeptService deptService;
    private final DictService dictService;

    /**
     * dict
     *
     * @param qo 问:
     * @return {@link R}<{@link Map}<{@link String}, {@link DictDetail}>>
     */
    @Operation(summary = "加载字典")
    @GetMapping("/dept/dict")
    public R<Map<String, DictDetail>> dict(CodesQo qo) {
        Map<String, DictDetail> map = dictService.loadDictDetail(qo);
        return R.success(map);
    }


    /**
     * 列表
     *
     * @param deptQo 部门请求对象
     * @return {@code R<List<SysDept>>}
     */
    @Operation(summary = "获取部门列表")
    //@MustPermission("system:dept:list")
    @GetMapping("/dept/list")
    public R<List<SysDept>> list(DeptQo deptQo) {
        return R.success(deptService.selectDeptList(deptQo));
    }

    @Operation(summary = "获取部门列表,树结构")
    //@MustPermission("system:dept:list")
    @GetMapping("/dept/listTree")
    public R<List<TreeNode<SysDept>>> selectDeptTree(DeptQo deptQo) {
        return R.success(deptService.selectDeptTree(deptQo));
    }

    @Operation(summary = "查询部门列表（排除节点）")
    //@MustPermission("system:dept:list")
    @GetMapping("/dept/list/exclude/{deptId}")
    public R<List<SysDept>> excludeChild(@PathVariable(value = "deptId", required = false) String deptId) {
        return R.success(deptService.excludeChild(deptId));
    }

    @Operation(summary = "根据部门编号获取详细信息")
    //@MustPermission("system:dept:query")
    @GetMapping(value = "/dept/{deptId}")
    public R<SysDept> getInfo(@PathVariable String deptId) {
        SysDept dept = _sys_dept.withBind().getById(deptId);
        return R.success(dept);
    }

    /**
     * 添加
     *
     * @param dept 部门
     * @return {@link R}<{@link Boolean}>
     */
    @Operation(summary = "新增部门")
    //@MustPermission("system:dept:add")
    @PostMapping("/dept")
    public R<Boolean> add(@Validated @RequestBody SysDept dept) {
        deptService.add(dept);
        return R.success();
    }

    @Operation(summary = "修改部门")
    //@MustPermission("system:dept:edit")
    @PutMapping("/dept")
    public R<Boolean> edit(@RequestBody SysDept dept) {
        deptService.update(dept);
        return R.success();
    }


    @Operation(summary = "删除部门")
    //@MustPermission("system:dept:remove")
    @DeleteMapping("/dept/{deptId}")
    public R<Boolean> remove(@PathVariable String deptId) {
        deptService.deleteDeptById(deptId);
        return R.success();
    }
}
