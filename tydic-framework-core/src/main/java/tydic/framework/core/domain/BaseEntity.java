package tydic.framework.core.domain;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tangzc.mpe.autotable.annotation.Column;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import tydic.framework.core.plugin.auth.AuthContext;
import tydic.framework.core.plugin.jackson.annotation.JsonNickname;
import tydic.framework.core.plugin.mybatis.MybatisPlusEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 基础实体类
 */
@Getter
@Setter
@EqualsAndHashCode
@FieldNameConstants
public abstract class BaseEntity implements Serializable, MybatisPlusEntity {

    /**
     * 创建人
     */
    @JsonNickname
    @Column(comment = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间", notNull = true)
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 最后更新人
     */
    @JsonNickname
    @Column(comment = "最后更新人")
    @ApiModelProperty("最后更新人")
    private String updateBy;

    /**
     * 最后更新时间
     */
    @Column(comment = "最后更新时间", notNull = true)
    @ApiModelProperty("最后更新时间")
    private Date updateTime;


    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public DateTime getCreateDateTime() {
        return this.createTime == null ? null : DateTime.of(this.createTime);
    }

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public DateTime getUpdateDateTime() {
        return this.updateTime == null ? null : DateTime.of(this.updateTime);
    }

    public BaseEntity copyBaseField(BaseEntity other) {
        if (other == null) {
            return this;
        }
        this.setCreateBy(other.getCreateBy());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateBy(other.getUpdateBy());
        this.setUpdateTime(other.getUpdateTime());
        return this;
    }

    @Override
    public void preInsert() {
        String account = AuthContext.getAccountOr("unknown");
        Date now = DateTime.now().setField(DateField.MILLISECOND, 0);
        this.createBy = account;
        this.updateBy = account;
        this.createTime = now;
        this.updateTime = now;
    }

    @Override
    public void preUpdate() {
        String account = AuthContext.getAccountOr("unknown");
        Date now = DateTime.now().setField(DateField.MILLISECOND, 0);
        this.updateBy = account;
        this.updateTime = now;
    }

    @Override
    public void preLambdaUpdate(Map<SFunction, Object> updateSets) {
        String account = AuthContext.getAccountOr("unknown");
        Date now = DateTime.now().setField(DateField.MILLISECOND, 0);
        SFunction<BaseEntity, String> getUpdateBy = BaseEntity::getUpdateBy;
        SFunction<BaseEntity, Date> getUpdateTime = BaseEntity::getUpdateTime;
        updateSets.put(getUpdateBy, account);
        updateSets.put(getUpdateTime, now);
    }
}
