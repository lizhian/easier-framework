package tydic.framework.test.eo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.BindEntityByMid;
import com.tangzc.mpe.bind.metadata.annotation.JoinOrderBy;
import com.tangzc.mpe.bind.metadata.annotation.MidCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import tydic.framework.core.domain.BaseEntity;
import tydic.framework.core.plugin.enums.Dict;
import tydic.framework.core.plugin.mybatis.MybatisPlusEntity;
import tydic.framework.test.enums.EnableStatus;
import tydic.framework.test.enums.SysAppType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
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

    @TableId
    @Column(comment = "应用主键")
    private String appId;

    @Column(comment = "应用编码", notNull = true)
    @NotBlank
    private String appCode;

    @Column(comment = "应用名称", notNull = true)
    @NotBlank
    private String appName;

    @Column(comment = "应用地址")
    @NotBlank
    private String appAddr;

    @Column(comment = "应用图标图片", type = "longtxt")
    @NotBlank
    private String appImage;

    @Column(comment = "应用排序", notNull = true, defaultValue = "100")
    private Integer sort;

    @Column(comment = "门户合集")
    @Dict(type = "portal_collection_type")
    private String portalCollectionType;

    @Column(comment = "启用状态", notNull = true)
    @NotNull
    private EnableStatus enableStatus;

    @Column(comment = "应用类型", notNull = true)
    @NotNull
    private SysAppType appType;

    @Column(comment = "应用描述")
    private String appDesc;


    @Column(comment = "应用描述")
    private List<SysApp> children;


    @Schema(description = "绑定的数据字典")
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


    private Date time;


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
