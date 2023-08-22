package easier.framework.test.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class UnLoginVo {
    @Schema(description = "重定向地址")
    private String redirect_uri;
    @Schema(description = "是否追加前端地址")
    private boolean append_frontend_uri;
    @Schema(description = "是否对前端地址编码")
    private boolean encode_frontend_uri;
}
