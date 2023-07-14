package easier.framework.test.eo;

import cn.hutool.core.lang.RegexPool;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseLogicEntity;
import easier.framework.core.plugin.dict.ShowDictDetail;
import easier.framework.core.plugin.tree.TreeBuilder;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.TreeUtil;
import easier.framework.test.enums.EnableStatus;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.*;
import java.util.List;


/**
 * 部门表
 * t_dept
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
@Table(value = "t_dept", comment = "部门表")
public class Dept extends BaseLogicEntity {

    @TableField(exist = false)
    public static TreeBuilder<Dept> treeBuilder = TreeBuilder
            .of(Dept.class)
            .key(Dept::getDeptId)
            .name(Dept::getDeptName)
            .parentKey(Dept::getParentId)
            .sort(Dept::getSort)
            .enable(it -> EnableStatus.enable.equals(it.getStatus()))
            .build();

    @Column(comment = "部门主键", notNull = true)
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String deptId;

    @Column(comment = "部门名称", notNull = true)
    @NotBlank
    @Size(max = 30)
    @Pattern(regexp = RegexPool.CHINESES, message = "必须由中文组成")
    private String deptName;

    @Column(comment = "父部门主键", notNull = true)
    private String parentId;

    @Column(comment = "祖先列表", notNull = true, length = 1024)
    private String ancestors;

    @NotNull
    @Column(comment = "排序", notNull = true, defaultValue = TreeUtil.DEFAULT_SORT_STR)
    private Integer sort;

    @Column(comment = "负责人")
    private String leader;

    @Column(comment = "电话")
    @Size(max = 11)
    private String phone;

    @Column(comment = "电子邮件")
    @Email
    @Size(max = 50)
    private String email;

    @Column(comment = "状态", notNull = true, defaultValue = EnableStatus.defaultValue)
    @NotNull
    @ShowDictDetail
    private EnableStatus status;

    public List<String> ancestorsAsList() {
        return StrUtil.smartSplit(this.ancestors);
    }
}
