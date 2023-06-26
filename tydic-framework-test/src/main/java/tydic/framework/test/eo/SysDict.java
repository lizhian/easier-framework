package tydic.framework.test.eo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import tydic.framework.core.domain.BaseLogicEntity;
import tydic.framework.core.plugin.jackson.annotation.AliasId;
import tydic.framework.test.enums.EnableStatus;
import tydic.framework.test.enums.SysDictType;

import javax.validation.constraints.NotBlank;


/**
 * 系统字典表
 */
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

    
}
