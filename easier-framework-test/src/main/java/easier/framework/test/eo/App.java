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
 * 应用表
 * t_app
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
@Table(value = "t_app", comment = "应用表")
public class App extends BaseLogicEntity {

    @Column(comment = "应用主键")
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String appId;

    @Column(comment = "应用编码", notNull = true)
    @TableCode
    @NotBlank
    private String appCode;

    @Column(comment = "应用名称")
    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = RegexPool.CHINESES, message = "必须由中文组成")
    private String appName;

    @Column(comment = "状态", notNull = true, defaultValue = EnableStatus.defaultValue)
    @NotNull
    @ShowDictDetail
    private EnableStatus status;

    @Column(comment = "排序", notNull = true, defaultValue = TreeUtil.DEFAULT_SORT_STR)
    @NotNull
    private Integer sort;

    @Column(comment = "访问地址")
    private String url;

    @Column(comment = "负责人")
    private String leader;

    @Column(comment = "手机号码")
    @Size(max = 11)
    private String phone;

    @Column(comment = "负责人")
    private String remark;

    @Schema(description = "关联的角色")
    @BindEntityByMid(
            conditions = @MidCondition(
                    midEntity = RoleApp.class
                    , selfField = App.Fields.appCode
                    , selfMidField = RoleApp.Fields.appCole
                    , joinMidField = RoleApp.Fields.roleCode
                    , joinField = Role.Fields.roleCode
            ),
            orderBy = @JoinOrderBy(field = Role.Fields.sort)
    )
    private List<Role> roles;

    @Schema(description = "关联的角色编码")
    @BindFieldByMid(
            conditions = @MidCondition(
                    midEntity = RoleApp.class
                    , selfField = App.Fields.appCode
                    , selfMidField = RoleApp.Fields.appCole
                    , joinMidField = RoleApp.Fields.roleCode
                    , joinField = Role.Fields.roleCode
            ),
            entity = Role.class,
            orderBy = @JoinOrderBy(field = Role.Fields.sort),
            field = Role.Fields.roleCode
    )
    private List<String> roleCodes;
}
