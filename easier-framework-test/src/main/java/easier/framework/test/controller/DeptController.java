package easier.framework.test.controller;

import easier.framework.core.domain.CodesQo;
import easier.framework.core.domain.IdQo;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.tree.TreeNode;
import easier.framework.test.eo.SysDept;
import easier.framework.test.qo.DeptTreeQo;
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
        Map<String, DictDetail> map = this.dictService.loadDictDetail(qo);
        return R.success(map);
    }


    /**
     * 获取部门树
     *
     * @param qo 请求对象
     * @return {@link R}<{@link List}<{@link TreeNode}<{@link SysDept}>>>
     */
    @Operation(summary = "获取部门树")
    @GetMapping("/dept/tree")
    public R<List<TreeNode<SysDept>>> getDeptTree(DeptTreeQo qo) {
        List<TreeNode<SysDept>> list = this.deptService.getDeptTree(qo);
        return R.success(list);
    }


    /**
     * 添加部门
     *
     * @param entity 实体
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "添加部门")
    @PostMapping("/dept/add")
    public R<String> addDept(@Validated @RequestBody SysDept entity) {
        this.deptService.addDept(entity);
        return R.success();
    }

    /**
     * 更新部门
     *
     * @param entity 实体
     * @return {@link R}<{@link String}>
     */
    @Operation(summary = "更新部门")
    @PutMapping("/dept/update")
    public R<String> updateDept(@RequestBody SysDept entity) {
        this.deptService.updateDept(entity);
        return R.success();
    }


    @Operation(summary = "删除部门")
    @DeleteMapping("/dept/delete")
    public R<String> deleteDept(@Validated IdQo qo) {
        this.deptService.deleteDept(qo.getId());
        return R.success();
    }
}
