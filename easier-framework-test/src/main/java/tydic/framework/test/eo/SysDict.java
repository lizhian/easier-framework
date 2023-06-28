package easier.framework.test.eo;


import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseLogicEntity;
import easier.framework.core.plugin.jackson.annotation.AliasId;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.enums.SysDictType;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;


/**
 * 系统字典表
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "sys_dict", dsName = "test", comment = "系统字典表")
public class SysDict extends BaseLogicEntity {

    @Column(comment = "应用主键")
    @AliasId
    @TableId
    private String dictId;

    @Column(comment = "字典编码", notNull = true)
    @NotBlank
    private String dictCode;

    @Column(comment = "字典名称", notNull = true)
    @NotBlank
    private String dictName;

    @Column(comment = "字典说明")
    private String dictDesc;

    @Column(comment = "字典类型", notNull = true)
    private SysDictType dictType;

    @Column(comment = "启用状态", notNull = true)
    private EnableStatus enableStatus;

    @Column(comment = "时间", notNull = true)
    private DateTime dateTime = DateTime.now();


}
