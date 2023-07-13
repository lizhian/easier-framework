package easier.framework.test.eo;

import cn.hutool.core.lang.RegexPool;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseLogicEntity;
import easier.framework.core.plugin.dict.ShowDictDetail;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.core.util.TreeUtil;
import easier.framework.test.enums.EnableStatus;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 角色
 * t_role
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "t_role", comment = "角色表")
public class Role extends BaseLogicEntity {

    @Column(comment = "角色主键", notNull = true)
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String roleId;

    @Column(comment = "应用编码", notNull = true)
    @NotBlank
    @Size(min = 6, max = 30)
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "必须以字母开头,且只能由小写字母、数字和下划线组成")
    private String roleCode;

    @Column(comment = "应用名称")
    @NotBlank
    @Size(min = 2, max = 8)
    @Pattern(regexp = RegexPool.CHINESE_NAME, message = "必须由中文组成")
    private String roleName;

    @Column(comment = "状态")
    @ShowDictDetail
    private EnableStatus status;

    @Column(comment = "角色类型")
    @ShowDictDetail("role_type")
    private String roleType;

    @Column(comment = "排序", notNull = true, defaultValue = TreeUtil.DEFAULT_SORT_STR)
    @NotNull
    private Integer sort;

    @Column(comment = "备注")
    private String remark;
}
