package easier.framework.test.eo;

import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseEntity;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;


/**
 * 角色应用表
 * t_role_app
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
@Table(value = "t_role_app", comment = "角色应用表")
public class RoleApp extends BaseEntity {


    @Column(comment = "角色编码", notNull = true)
    @NotBlank
    private String roleCode;

    @Column(comment = "应用编码", notNull = true)
    @NotBlank
    private String appCole;
}
