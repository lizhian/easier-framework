package easier.framework.test.eo;

import cn.hutool.core.lang.RegexPool;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseLogicEntity;
import easier.framework.core.plugin.dict.ShowDictDetail;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.enums.SexType;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 用户对象 sys_user
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "sys_user", comment = "用户表")
public class SysUser extends BaseLogicEntity {

    /**
     * 用户ID
     */
    @Column(comment = "用户主键", notNull = true)
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private Long userId;

    @Column(comment = "用户账号", notNull = true)
    @NotBlank
    @Size(min = 6, max = 30)
    @Pattern(regexp = "^[a-z][a-z0-9]*$", message = "必须以字母开头,且只能由小写字母和数字组成")
    private String username;

    /**
     * 用户昵称
     */
    @Column(comment = "用户名称")
    @NotBlank
    @Size(min = 2, max = 8)
    @Pattern(regexp = RegexPool.CHINESE_NAME, message = "必须由中文组成")
    private String nickname;

    @Column(comment = "所属部门")
    private Long deptId;

    @Column(comment = "用户邮箱")
    @Email
    @Size(max = 50)
    private String email;

    @Column(comment = "手机号码")
    @Size(max = 11)
    private String phoneNumber;

    @Column(comment = "用户性别")
    @ShowDictDetail
    private SexType sexType;

    @Column(comment = "用户头像", type = "text")
    private String avatar;

    @Column(comment = "密码")
    private String password;

    @Column(comment = "帐号状态")
    @ShowDictDetail
    private EnableStatus status;

    @Column(comment = "最后登录IP")
    private String loginIp;

    @Column(comment = "最后登录时间")
    private Date loginDate;
}
