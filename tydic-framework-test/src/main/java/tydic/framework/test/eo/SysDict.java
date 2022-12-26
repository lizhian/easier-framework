package tydic.framework.test.eo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.actable.annotation.Column;
import com.tangzc.mpe.actable.annotation.Table;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import tydic.framework.core.domain.BaseLogicEntity;
import tydic.framework.core.plugin.jackson.annotation.JsonID;
import tydic.framework.test.enums.EnableStatus;
import tydic.framework.test.enums.SysDictType;


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

    @ApiModelProperty("字典主键")
    @Column(comment = "应用主键")
    @JsonID
    @TableId
    private String dictId;

    @ApiModelProperty("字典编码")
    @Column(comment = "字典编码", notNull = true)
    @NotBlank
    private String dictCode;

    @ApiModelProperty("字典名称")
    @Column(comment = "字典名称", notNull = true)
    @NotBlank
    private String dictName;

    @ApiModelProperty("字典说明")
    @Column(comment = "字典说明")
    private String dictDesc;

    @ApiModelProperty("字典类型")
    @Column(comment = "字典类型", notNull = true)
    private SysDictType dictType;

    @ApiModelProperty("启用状态")
    @Column(comment = "启用状态", notNull = true)
    private EnableStatus enableStatus;

}
