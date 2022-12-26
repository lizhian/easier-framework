package tydic.framework.core.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.tangzc.mpe.actable.annotation.Column;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

/**
 * 逻辑删除基础实体类
 */
@Getter
@EqualsAndHashCode
@FieldNameConstants
public abstract class BaseLogicEntity extends BaseEntity {

    @ApiModelProperty(hidden = true)
    @Column(comment = "逻辑删除标识", notNull = true, defaultValue = "0", length = 1)
    @TableLogic
    private Integer deleted;

    @JsonSetter
    public BaseLogicEntity setDeleted(Integer deleted) {
        if (deleted == null) {
            this.deleted = null;
            return this;
        }
        this.deleted = deleted <= 0 ? 0 : 1;
        return this;
    }

    public BaseLogicEntity setDeleted(Boolean deleted) {
        if (deleted == null) {
            this.deleted = null;
            return this;
        }
        this.deleted = deleted ? 1 : 0;
        return this;
    }

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public boolean isLogicDeleted() {
        return this.deleted != null && this.deleted > 0;
    }

    @Override
    public BaseLogicEntity copyBaseField(BaseEntity other) {
        super.copyBaseField(other);
        if (other instanceof BaseLogicEntity otherBaseLogicEntity) {
            this.setDeleted(otherBaseLogicEntity.getDeleted());
        }
        return this;
    }
}
