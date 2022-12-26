package tydic.framework.test.eo.relation;

import com.tangzc.mpe.actable.annotation.Column;
import com.tangzc.mpe.actable.annotation.Table;
import com.tangzc.mpe.datasource.annotation.Condition;
import com.tangzc.mpe.datasource.annotation.DataSource;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import tydic.framework.core.domain.BaseEntity;
import tydic.framework.core.plugin.mybatis.RelatedDeleteCondition;
import tydic.framework.test.eo.SysApp;
import tydic.framework.test.eo.SysDict;


/**
 * 应用-字典 关联表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "app_to_dict", dsName = "test", comment = "应用-字典 关联表")
public class AppToDict extends BaseEntity {

    @ApiModelProperty("应用编号")
    @NotBlank
    @RelatedDeleteCondition(source = SysApp.class, sourceField = SysApp.Fields.appCode)
    private String appCode;

    @ApiModelProperty("应用名称")
    @Column(comment = "应用名称")
    @DataSource(
            source = SysApp.class
            , field = SysApp.Fields.appName
            , conditions = @Condition(selfField = Fields.appCode, sourceField = SysApp.Fields.appCode)
    )
    private String appName;

    @ApiModelProperty("字典编码")
    @NotBlank
    @RelatedDeleteCondition(source = SysDict.class, sourceField = SysDict.Fields.dictCode)
    private String dictCode;
}
