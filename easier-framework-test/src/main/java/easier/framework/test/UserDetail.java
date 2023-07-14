package easier.framework.test;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail {

    @Schema(description = "用户主键")
    private String userId;

    @Schema(description = "用户账号")
    private String username;


    @Schema(description = "用户名称")
    private String nickname;
}
