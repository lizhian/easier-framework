package easier.framework.test.eo;

import cn.hutool.core.lang.RegexPool;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.BindEntityByMid;
import com.tangzc.mpe.bind.metadata.annotation.BindFieldByMid;
import com.tangzc.mpe.bind.metadata.annotation.JoinOrderBy;
import com.tangzc.mpe.bind.metadata.annotation.MidCondition;
import easier.framework.core.domain.BaseLogicEntity;
import easier.framework.core.plugin.jackson.annotation.ShowDictDetail;
import easier.framework.core.plugin.mybatis.TableCode;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.core.util.TreeUtil;
import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

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

    @Column(comment = "角色编码", notNull = true)
    @TableCode
    @NotBlank
    private String roleCode;

    @Column(comment = "角色名称")
    @NotBlank
    @Size(min = 2, max = 8)
    @Pattern(regexp = RegexPool.CHINESES, message = "必须由中文组成")
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


    @Schema(description = "关联的应用")
    @BindEntityByMid(
            conditions = @MidCondition(
                    midEntity = RoleApp.class,
                    selfField = Role.Fields.roleCode,
                    selfMidField = RoleApp.Fields.roleCode,
                    joinMidField = RoleApp.Fields.appCole,
                    joinField = App.Fields.appCode
            ),
            orderBy = @JoinOrderBy(field = App.Fields.sort)
    )
    private List<App> apps;

    @Schema(description = "关联的应用编码")
    @BindFieldByMid(
            conditions = @MidCondition(
                    midEntity = RoleApp.class,
                    selfField = Role.Fields.roleCode,
                    selfMidField = RoleApp.Fields.roleCode,
                    joinMidField = RoleApp.Fields.appCole,
                    joinField = App.Fields.appCode
            ),
            orderBy = @JoinOrderBy(field = App.Fields.sort),
            entity = App.class,
            field = App.Fields.appCode
    )
    private List<String> appCodes;
}
