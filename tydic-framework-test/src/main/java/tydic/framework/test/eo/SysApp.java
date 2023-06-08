package tydic.framework.test.eo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.tangzc.mpe.actable.annotation.Column;
import com.tangzc.mpe.actable.annotation.Table;
import com.tangzc.mpe.actable.annotation.constants.MySqlTypeConstant;
import com.tangzc.mpe.bind.metadata.annotation.BindEntityByMid;
import com.tangzc.mpe.bind.metadata.annotation.JoinOrderBy;
import com.tangzc.mpe.bind.metadata.annotation.MidCondition;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import tydic.framework.core.domain.BaseEntity;
import tydic.framework.core.plugin.enums.Dict;
import tydic.framework.core.plugin.jackson.annotation.JsonID;
import tydic.framework.core.plugin.mybatis.MybatisPlusEntity;
import tydic.framework.test.enums.EnableStatus;
import tydic.framework.test.enums.SysAppType;

import java.util.List;
import java.util.Map;

/**
 * 系统应用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "sys_app", dsName = "test", comment = "系统应用表")
public class SysApp implements MybatisPlusEntity {


    @ApiModelProperty("应用主键")
    @Column(comment = "应用主键")
    @JsonID
    @TableId
    private String appId;

    @ApiModelProperty("应用编码")
    @Column(comment = "应用编码", notNull = true)
    @NotBlank
    //@TableCode
    //@CodeValid
    private String appCode;

    @ApiModelProperty("应用名称")
    @Column(comment = "应用名称", notNull = true)
    @NotBlank
    private String appName;

    @ApiModelProperty("应用地址")
    @Column(comment = "应用地址")
    @NotBlank
    private String appAddr;

    @ApiModelProperty("应用图标图片")
    @Column(comment = "应用图标图片", type = MySqlTypeConstant.LONGTEXT)
    @NotBlank
    private String appImage;

    @ApiModelProperty("应用排序")
    @Column(comment = "应用排序", notNull = true, defaultValue = "100")
    private Integer sort;

    @ApiModelProperty("门户合集")
    @Column(comment = "门户合集")
    @Dict(type = "portal_collection_type")
    private String portalCollectionType;

    @ApiModelProperty("启用状态")
    @Column(comment = "启用状态", notNull = true)
    @NotNull
    private EnableStatus enableStatus;

    @ApiModelProperty("应用类型")
    @Column(comment = "应用类型", notNull = true)
    @NotNull
    private SysAppType appType;

    @ApiModelProperty("应用描述")
    @Column(comment = "应用描述")
    private String appDesc;


    @ApiModelProperty("应用描述")
    @Column(comment = "应用描述")
    private List<SysApp> children;


    @ApiModelProperty("绑定的数据字典")
    @BindEntityByMid(
            conditions = @MidCondition(
                    midEntity = AppToDict.class
                    , selfField = AppToDict.Fields.appCode
                    , selfMidField = AppToDict.Fields.appCode
                    , joinMidField = AppToDict.Fields.dictCode
                    , joinField = SysDict.Fields.dictCode
            )
            , orderBy = @JoinOrderBy(field = BaseEntity.Fields.updateTime, isAsc = false)
    )
    private List<SysDict> dictList;


    public static void main(String[] args) {
        SysApp sysApp = new SysApp();
    }

    public String getParameter(String s) {
        return null;
    }

    @Override
    public void preInsert() {

    }

    @Override
    public void preUpdate() {

    }

    @Override
    public void preLambdaUpdate(Map<SFunction, Object> updateSets) {

    }
}
