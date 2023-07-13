package easier.framework.test.eo;

import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseEntity;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;


/**
 * 用户角色表
 * t_user_role
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
@Table(value = "t_user_role", comment = "用户角色表")
public class UserRole extends BaseEntity {

    @Column(comment = "用户账号", notNull = true)
    @NotBlank
    private String username;

    @Column(comment = "角色编码", notNull = true)
    @NotBlank
    private String roleCode;
}
