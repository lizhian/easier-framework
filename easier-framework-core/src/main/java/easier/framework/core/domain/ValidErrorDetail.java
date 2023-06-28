package easier.framework.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 字段校验错误详情
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class ValidErrorDetail {
    private String fieldName;
    private String fieldLabel;
    private String message;
    private String mergeMessage;
}
