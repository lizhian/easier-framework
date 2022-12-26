package tydic.framework.core.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

/**
 * 范围体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class Between<T> implements Serializable {
    @ApiModelProperty("最小值")
    private T min;

    @ApiModelProperty("包含最小值")
    @Builder.Default
    private boolean includeMin = true;

    @ApiModelProperty("最大值")
    private T max;

    @ApiModelProperty("包含最大值")
    @Builder.Default
    private boolean includeMax = true;

    public static <T> Between<T> of(T min, T max) {
        return Between.<T>builder()
                      .min(min)
                      .includeMin(true)
                      .max(max)
                      .includeMax(true)
                      .build();
    }

    public boolean hasMin() {
        return this.min != null;
    }

    public boolean hasMax() {
        return this.max != null;
    }
}
