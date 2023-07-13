package easier.framework.test.eo;

import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseEntity;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;


/**
 * 角色菜单表
 * t_role_menu
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
@Table(value = "t_role_menu", comment = "角色菜单表")
public class RoleMenu extends BaseEntity {


    @Column(comment = "角色编码", notNull = true)
    @NotBlank
    private String roleCode;

    @Column(comment = "应用编码", notNull = true)
    @NotBlank
    private String appCole;

    @Column(comment = "菜单主键", notNull = true)
    @NotBlank
    private String menuId;
}
