package easier.framework.test.service;

import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.plugin.tree.TreeNode;
import easier.framework.core.util.ExtensionCore;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.TreeUtil;
import easier.framework.core.util.ValidUtil;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.eo.SysDept;
import easier.framework.test.qo.DeptTreeQo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * 部门服务
 *
 * @author lizhian
 * @date 2023/07/05
 */
@Service
@RequiredArgsConstructor
@ExtensionMethod(ExtensionCore.class)
public class DeptService {
    private final Repo<SysDept> _sys_dept = Repos.of(SysDept.class);


    /**
     * 获取部门树
     *
     * @param qo 请求对象
     * @return {@link List}<{@link TreeNode}<{@link SysDept}>>
     */
    public List<TreeNode<SysDept>> getDeptTree(DeptTreeQo qo) {
        List<SysDept> list = this._sys_dept.newQuery()
                .whenNotNull()
                .eq(SysDept::getStatus, qo.getStatus())
                .end()
                .list();
        List<TreeNode<SysDept>> tree = SysDept.treeBuilder.build(list);
        String excludeDeptId = qo.getExcludeDeptId();
        String deptName = qo.getDeptName();
        tree = SysDept.treeBuilder.match(tree, dept -> {
            if (StrUtil.isNotBlank(deptName)) {
                return dept.getDeptName().orEmpty().contains(deptName);
            }
            return true;
        });
        tree = SysDept.treeBuilder.exclude(tree, dept -> {
            if (StrUtil.isNotBlank(excludeDeptId)) {
                return excludeDeptId.equals(dept.getDeptId());
            }
            return false;
        });
        return tree;
    }

    /**
     * 添加部门
     *
     * @param entity 实体
     */
    public void addDept(SysDept entity) {
        ValidUtil.valid(entity);
        if (entity.getParentId().isBlank()) {
            entity.setParentId(TreeUtil.ROOT_KEY);
        }
        boolean existsSameName = this._sys_dept.newQuery()
                .eq(SysDept::getParentId, entity.getParentId())
                .eq(SysDept::getDeptName, entity.getDeptName())
                .exists();
        if (existsSameName) {
            throw BizException.of("新增部门'{}'失败，部门名称已存在", entity.getDeptName());
        }
        SysDept parent = this._sys_dept.getById(entity.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (parent != null && EnableStatus.isDisable(parent.getStatus())) {
            throw BizException.of("部门停用，不允许新增");
        }
        String ancestors;
        if (parent == null) {
            ancestors = entity.getParentId();
        } else {
            ancestors = parent.getAncestors() + "," + entity.getParentId();
        }
        entity.setAncestors(ancestors);
        this._sys_dept.add(entity);
    }

    /**
     * 更新
     * 修改部门管理信息
     *
     * @param entity 实体
     */
    public void updateDept(SysDept entity) {
        ValidUtil.validOnUpdate(entity);
        String deptId = entity.getDeptId();
        SysDept old = this._sys_dept.getById(deptId);
        if (old == null) {
            throw BizException.of("无效的部门id:{}", deptId);
        }
        //更新了父节点
        boolean updateParentId = !old.getParentId().orEmpty().equals(entity.getParentId());
        //更新了用户名
        boolean updateDeptName = !old.getDeptName().orEmpty().equals(entity.getDeptName());
        //更新了状态
        boolean updateStatus = !Objects.equals(old.getStatus(), entity.getStatus());

        //判断名称是否重复
        if (updateDeptName || updateParentId) {
            this._sys_dept.newQuery()
                    .ne(SysDept::getDeptId, deptId)
                    .eq(SysDept::getParentId, entity.getParentId())
                    .eq(SysDept::getDeptName, entity.getDeptName())
                    .existsThenThrow("修改部门'{}'失败，部门名称已存在", entity.getDeptName());
        }

        String oldAncestors = old.getAncestors();
        if (updateParentId) {
            if (deptId.equals(entity.getParentId())) {
                throw BizException.of("修改部门'{}'失败，上级部门不能是自己", entity.getDeptName());
            }
            SysDept newParent = this._sys_dept.getById(entity.getParentId());
            if (newParent == null) {
                throw BizException.of("无效的上级部门:{}", entity.getParentId());
            }
            if (newParent.ancestorsAsList().contains(deptId)) {
                throw BizException.of("新的上级部门'{}'不能是自己的子部门", newParent.getDeptName());
            }
            String newAncestors = newParent.getAncestors() + "," + entity.getParentId();
            this.updateDescendants(deptId, oldAncestors, newAncestors);

            SysDept oldParent = this._sys_dept.getById(old.getParentId());
        }

        if (updateStatus) {
            //状态改为启用,所有父节点都改成启用
            if (EnableStatus.isEnable(entity.getStatus())) {
                /*this._sys_dept.newUpdate()
                        .set()*/
            }
        }

        if (entity.getParentId().equals(deptId)) {
            throw BizException.of("修改部门'{}'失败，上级部门不能是自己", entity.getDeptName());
        }
        if (entity.getStatus().equals(EnableStatus.disable)) {
            boolean existsEnableChildren = this._sys_dept.newQuery()
                    .eq(SysDept::getStatus, EnableStatus.enable)
                    .likeLeft(SysDept::getAncestors, deptId)
                    .exists();
            if (existsEnableChildren) {
                throw BizException.of("无法停用该部门,因为该部门包含未停用的子部门！");
            }
        }
        this._sys_dept.update(entity);
    }


    public void deleteDept(String deptId) {
        if (deptId.isBlank()) {
            throw BizException.of("部门ID不能为空");
        }
        if (this._sys_dept.existsBy(SysDept::getParentId, deptId)) {
            throw BizException.of("存在下级部门,不允许删除");
        }
        //todo 部门存在用户,不允许删除
        this._sys_dept.deleteById(deptId);
    }

    /**
     * 更新后代
     *
     * @param deptId       部门id
     * @param oldAncestors 老祖宗
     * @param newAncestors 新祖先
     */
    private void updateDescendants(String deptId, String oldAncestors, String newAncestors) {

    }

}
