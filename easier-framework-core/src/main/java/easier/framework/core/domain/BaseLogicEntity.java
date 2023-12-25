package easier.framework.core.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tangzc.mpe.autotable.annotation.Column;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * 逻辑删除基础实体类
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
public abstract class BaseLogicEntity extends BaseEntity {

    @Schema(hidden = true)
    @Column(comment = "逻辑删除标识", notNull = true, defaultValue = "0", length = 1)
    @TableLogic
    private Boolean deleted;

    @JsonIgnore
    public boolean isLogicDeleted() {
        return this.deleted != null && this.deleted;
    }

    @Override
    public BaseLogicEntity copyBaseField(BaseEntity other) {
        super.copyBaseField(other);
        if (other instanceof BaseLogicEntity) {
            BaseLogicEntity otherBaseLogicEntity = (BaseLogicEntity) other;
            this.setDeleted(otherBaseLogicEntity.getDeleted());
        }
        return this;
    }

    @Override
    public void preInsert() {
        super.preInsert();
        if (this.deleted == null) {
            this.deleted = false;
        }
    }
}
