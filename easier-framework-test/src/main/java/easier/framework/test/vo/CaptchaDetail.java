package easier.framework.test.vo;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class CaptchaDetail {
    private String captchaId;
    private String imageBase64Data;
}
