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
import easier.framework.core.plugin.dict.ShowDictDetail;
import easier.framework.core.plugin.mybatis.TableCode;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.test.cache.ShowDeptDetail;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.enums.SexType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * 用户表
 * t_user
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "t_user", comment = "用户表")
public class User extends BaseLogicEntity {

    /**
     * 用户ID
     */
    @Column(comment = "用户主键", notNull = true)
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String userId;

    @Column(comment = "用户账号", notNull = true)
    @TableCode
    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[a-z][a-z0-9]*$", message = "必须以字母开头,且只能由小写字母和数字组成")
    private String username;


    @Column(comment = "用户名称")
    @NotBlank
    @Size(min = 2, max = 8)
    @Pattern(regexp = RegexPool.CHINESE_NAME, message = "必须为中文姓名")
    private String nickname;

    @Column(comment = "所属部门")
    @ShowDeptDetail
    private String deptId;

    @Column(comment = "用户邮箱")
    @Size(max = 50)
    @Email
    private String email;

    @Column(comment = "手机号码")
    @Size(max = 11)
    @Pattern(regexp = RegexPool.MOBILE, message = "错误的手机号码格式")
    private String phone;

    @Column(comment = "身份证")
    @Size(max = 18)
    @Pattern(regexp = RegexPool.CITIZEN_ID, message = "错误的身份证格式")
    private String idCard;

    @Column(comment = "用户性别")
    @ShowDictDetail
    private SexType sexType;

    @Column(comment = "用户头像", type = "text")
    private String avatar;

    @Column(comment = "密码")
    private String password;

    @Column(comment = "帐号状态")
    @NotNull
    @ShowDictDetail
    private EnableStatus status;

    @Column(comment = "最后登录IP")
    private String lastLoginIp;

    @Column(comment = "最后登录时间")
    private Date lastLoginTime;


    @Schema(description = "关联的角色")
    @BindEntityByMid(
            conditions = @MidCondition(
                    midEntity = UserRole.class
                    , selfField = User.Fields.username
                    , selfMidField = UserRole.Fields.username
                    , joinMidField = UserRole.Fields.roleCode
                    , joinField = Role.Fields.roleCode
            ),
            orderBy = @JoinOrderBy(field = Role.Fields.sort)
    )
    private List<Role> roles;

    @Schema(description = "关联的角色编码")
    @BindFieldByMid(
            field = Role.Fields.roleCode,
            conditions = @MidCondition(
                    midEntity = UserRole.class
                    , selfField = Fields.username
                    , selfMidField = UserRole.Fields.username
                    , joinMidField = UserRole.Fields.roleCode
                    , joinField = Role.Fields.roleCode
            ),
            orderBy = @JoinOrderBy(field = Role.Fields.sort)
    )
    private List<Role> roleCodes;
}
