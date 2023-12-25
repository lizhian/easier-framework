package easier.framework.core.domain;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tangzc.mpe.autotable.annotation.Column;
import easier.framework.core.Easier;
import easier.framework.core.plugin.jackson.annotation.ShowUserDetail;
import easier.framework.core.plugin.mybatis.MybatisPlusEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 基础实体类
 */
@Getter
@Setter
@FieldNameConstants
public abstract class BaseEntity implements Serializable, MybatisPlusEntity {

    /**
     * 创建人
     */
    @ShowUserDetail
    @Column(comment = "创建人")
    @Schema(hidden = true)
    private String createBy;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间", notNull = true)
    @Schema(hidden = true)
    private Date createTime;

    /**
     * 最后更新人
     */
    @ShowUserDetail
    @Column(comment = "最后更新人")
    @Schema(hidden = true)
    private String updateBy;

    /**
     * 最后更新时间
     */
    @Column(comment = "最后更新时间", notNull = true)
    @Schema(hidden = true)
    private Date updateTime;


    @JsonIgnore
    public DateTime getCreateDateTime() {
        return this.createTime == null ? null : DateTime.of(this.createTime);
    }

    @JsonIgnore
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
        String account = Easier.Auth.getAccountOr("unknown");
        Date now = DateTime.now().setField(DateField.MILLISECOND, 0);
        this.createBy = account;
        this.updateBy = account;
        this.createTime = now;
        this.updateTime = now;
    }

    @Override
    public void preUpdate() {
        String account = Easier.Auth.getAccountOr("unknown");
        Date now = DateTime.now().setField(DateField.MILLISECOND, 0);
        this.updateBy = account;
        this.updateTime = now;
    }

    @Override
    public void preLambdaUpdate(Map<SFunction, Object> updateSets) {
        String account = Easier.Auth.getAccountOr("unknown");
        Date now = DateTime.now().setField(DateField.MILLISECOND, 0);
        SFunction<BaseEntity, String> getUpdateBy = BaseEntity::getUpdateBy;
        SFunction<BaseEntity, Date> getUpdateTime = BaseEntity::getUpdateTime;
        updateSets.put(getUpdateBy, account);
        updateSets.put(getUpdateTime, now);
    }
}
