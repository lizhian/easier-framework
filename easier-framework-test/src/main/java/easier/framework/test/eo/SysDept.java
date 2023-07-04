package easier.framework.test.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseLogicEntity;
import easier.framework.core.plugin.tree.TreeBuilder;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.core.util.TreeUtil;
import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门表 sys_dept
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "sys_dept", comment = "部门表")
public class SysDept extends BaseLogicEntity {

    @TableField(exist = false)
    public static TreeBuilder<SysDept> treeBuilder = TreeBuilder
            .of(SysDept.class)
            .key(SysDept::getDeptId)
            .name(SysDept::getDeptName)
            .parentKey(SysDept::getParentId)
            .sort(SysDept::getSort)
            .enable(it -> EnableStatus.enable.equals(it.getStatus()))
            .children(SysDept::setChildren)
            .build();


    /**
     * 部门ID
     */
    @Column(comment = "部门ID", notNull = true)
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String deptId;


    @Column(comment = "父部门ID", notNull = true)
    private String parentId;


    @Column(comment = "祖级列表", notNull = true)
    private String ancestors;


    @NotBlank
    @Size(max = 30)
    @Column(comment = "部门名称", notNull = true)
    private String deptName;


    /**
     * 显示顺序
     */
    @NotNull
    @Column(comment = "顺序", notNull = true, defaultValue = TreeUtil.DEFAULT_SORT_STR)
    private Integer sort;

    /**
     * 负责人
     */
    @Column(comment = "负责人")
    private String leader;

    /**
     * 联系电话
     */
    @Size(max = 11)
    @Column(comment = "联系电话", length = 11)
    private String phone;

    /**
     * 邮箱
     */
    @Email
    @Size(max = 50)
    @Column(comment = "邮箱", length = 50)
    private String email;

    @Column(comment = "部门状态", notNull = true, defaultValue = EnableStatus.defaultValue)
    @NotNull
    private EnableStatus status;


    @TableField(exist = false)
    @Schema(description = "子部门")
    private List<SysDept> children = new ArrayList<>();
}
