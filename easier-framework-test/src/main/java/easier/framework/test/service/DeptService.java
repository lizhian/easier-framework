package easier.framework.test.service;

import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.plugin.tree.TreeNode;
import easier.framework.core.util.ExtensionMethodUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.TreeUtil;
import easier.framework.core.util.ValidUtil;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.eo.SysDept;
import easier.framework.test.qo.DeptQo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 部门服务
 *
 * @author lizhian
 * @date 2023/07/05
 */
@Service
@RequiredArgsConstructor
@ExtensionMethod(ExtensionMethodUtil.class)
public class DeptService {
    /**
     * 系统部门
     */
    private final Repo<SysDept> _sys_dept = Repos.of(SysDept.class);


    /**
     * 选择部门列表
     *
     * @param deptQo 部门请求对象
     * @return {@code List<SysDept>}
     */
    public List<SysDept> selectDeptList(DeptQo deptQo) {
        return _sys_dept.newQuery()
                .whenNotBlank()
                .eq(SysDept::getParentId, deptQo.getParentId())
                .like(SysDept::getDeptName, deptQo.getDeptName())
                .whenNotNull()
                .like(SysDept::getStatus, deptQo.getStatus())
                .end()
                .list();

    }

    /**
     * 选择部门树
     *
     * @param deptQo 部门请求对象
     * @return {@code List<TreeNode<SysDept>>}
     */
    public List<TreeNode<SysDept>> selectDeptTree(DeptQo deptQo) {
        List<SysDept> list = this.selectDeptList(deptQo);
        return SysDept.treeBuilder.build(list);
    }

    /**
     * 排除孩子
     *
     * @param deptId 部门id
     * @return {@code List<SysDept>}
     */
    public List<SysDept> excludeChild(String deptId) {
        List<SysDept> all = _sys_dept.listAll();
        if (deptId.isBlank()) {
            return all;
        }
        return all.stream()
                .filter(it -> {
                    if (it.getDeptId().equals(deptId)) {
                        return false;
                    }
                    List<String> ancestors = StrUtil.smartSplit(it.getAncestors());
                    return !ancestors.contains(deptId);
                })
                .collect(Collectors.toList());
    }


    /**
     * 添加
     * 新增保存部门信息
     *
     * @param entity 实体
     */
    public void add(SysDept entity) {
        ValidUtil.valid(entity);
        if (entity.getParentId().isBlank()) {
            entity.setParentId(TreeUtil.DEFAULT_SORT_STR);
        }
        boolean existsSameDeptName = _sys_dept.newQuery()
                .eq(SysDept::getParentId, entity.getParentId())
                .eq(SysDept::getDeptName, entity.getDeptName())
                .exists();
        if (existsSameDeptName) {
            throw BizException.of("新增部门'{}'失败，部门名称已存在", entity.getDeptName());
        }
        SysDept parent = _sys_dept.getById(entity.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (parent != null && !EnableStatus.enable.equals(parent.getStatus())) {
            throw BizException.of("部门停用，不允许新增");
        }
        if (parent == null) {
            entity.setAncestors(entity.getParentId());
        } else {
            entity.setAncestors(parent.getAncestors() + "," + entity.getParentId());
        }
        _sys_dept.add(entity);
    }

    /**
     * 更新
     * 修改部门管理信息
     *
     * @param entity 实体
     */
    public void update(SysDept entity) {
        ValidUtil.validOnUpdate(entity);
        boolean existsSameDeptName = _sys_dept.newQuery()
                .ne(SysDept::getDeptId, entity.getDeptId())
                .eq(SysDept::getParentId, entity.getParentId())
                .eq(SysDept::getDeptName, entity.getDeptName())
                .exists();
        if (existsSameDeptName) {
            throw BizException.of("修改部门'{}'失败，部门名称已存在", entity.getDeptName());
        }
        if (entity.getParentId().equals(entity.getDeptId())) {
            throw BizException.of("修改部门'{}'失败，上级部门不能是自己", entity.getDeptName());
        }
        if (entity.getStatus().equals(EnableStatus.disable)) {
            Long count = _sys_dept.newQuery()
                    .eq(SysDept::getStatus, EnableStatus.enable)
                    .likeLeft(SysDept::getAncestors, entity.getAncestors())
                    .count();
            if (count > 0) {
                throw BizException.of("该部门包含未停用的子部门！");
            }
        }
        _sys_dept.update(entity);
    }

    /**
     * 删除部门通过id
     * 删除部门管理信息
     *
     * @param deptId 部门id
     */
    public void deleteDeptById(String deptId) {
        if (deptId.isBlank()) {
            throw BizException.of("部门ID不能为空");
        }
        if (_sys_dept.existsBy(SysDept::getParentId, deptId)) {
            throw BizException.of("存在下级部门,不允许删除");
        }
        //todo 部门存在用户,不允许删除
        _sys_dept.deleteById(deptId);
    }


}
