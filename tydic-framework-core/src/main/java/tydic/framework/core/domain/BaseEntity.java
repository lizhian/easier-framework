package tydic.framework.core.domain;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tangzc.mpe.actable.annotation.Column;
import com.tangzc.mpe.annotation.InsertOptionDate;
import com.tangzc.mpe.annotation.InsertOptionUser;
import com.tangzc.mpe.annotation.InsertUpdateOptionDate;
import com.tangzc.mpe.annotation.InsertUpdateOptionUser;
import com.tangzc.mpe.annotation.handler.IOptionByAutoFillHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import tydic.framework.core.plugin.jackson.annotation.JsonNickname;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类
 */
@Getter
@Setter
@EqualsAndHashCode
@FieldNameConstants
public abstract class BaseEntity implements Serializable {

    /**
     * 创建人
     */
    @JsonNickname
    @InsertOptionUser(IOptionByAutoFillHandler.class)
    @Column(comment = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @InsertOptionDate
    @Column(comment = "创建时间", notNull = true)
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 最后更新人
     */
    @JsonNickname
    @InsertUpdateOptionUser(IOptionByAutoFillHandler.class)
    @Column(comment = "最后更新人")
    @ApiModelProperty("最后更新人")
    private String updateBy;

    /**
     * 最后更新时间
     */
    @InsertUpdateOptionDate
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
}
