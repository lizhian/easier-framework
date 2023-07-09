package easier.framework.test.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseLogicEntity;
import easier.framework.core.plugin.tree.TreeBuilder;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.TreeUtil;
import easier.framework.test.enums.EnableStatus;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * 系统部门
 *
 * @author lizhian
 * @date 2023/07/05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "sys_dept", comment = "部门表")
public class SysDept extends BaseLogicEntity {

    /**
     * 树构建器
     */
    @TableField(exist = false)
    public static TreeBuilder<SysDept> treeBuilder = TreeBuilder
            .of(SysDept.class)
            .key(SysDept::getDeptId)
            .name(SysDept::getDeptName)
            .parentKey(SysDept::getParentId)
            .sort(SysDept::getSort)
            .enable(it -> EnableStatus.enable.equals(it.getStatus()))
            .build();


    /**
     * 部门id
     */
    @Column(comment = "部门ID", notNull = true)
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String deptId;

    /**
     * 部门名称
     */
    @NotBlank
    @Size(max = 30)
    @Column(comment = "部门名称", notNull = true)
    private String deptName;


    /**
     * 父id
     */
    @Column(comment = "父部门ID", notNull = true)
    private String parentId;


    /**
     * 祖先
     */
    @Column(comment = "祖先列表", notNull = true, length = 1024)
    private String ancestors;

    /**
     * 排序
     */
    @NotNull
    @Column(comment = "排序", notNull = true, defaultValue = TreeUtil.DEFAULT_SORT_STR)
    private Integer sort;

    /**
     * 负责人
     */
    @Column(comment = "负责人")
    private String leader;

    /**
     * 电话
     */
    @Size(max = 11)
    @Column(comment = "电话", length = 11)
    private String phone;

    /**
     * 电子邮件
     */
    @Email
    @Size(max = 50)
    @Column(comment = "电子邮件", length = 50)
    private String email;

    /**
     * 状态
     */
    @Column(comment = "状态", notNull = true, defaultValue = EnableStatus.defaultValue)
    @NotNull
    private EnableStatus status;


    /**
     * 祖先列表
     *
     * @return {@link List}<{@link String}>
     */
    public List<String> ancestorsAsList() {
        return StrUtil.smartSplit(this.ancestors);
    }
}
