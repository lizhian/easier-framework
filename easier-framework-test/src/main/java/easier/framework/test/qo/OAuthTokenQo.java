package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OAuthTokenQo {

    @Schema(description = "应用主键")
    @NotBlank
    private String client_id;
    @Schema(description = "应用密钥")
    @NotBlank
    private String client_secret;
    @Schema(description = "code授权码")
    @NotBlank(groups = CodeGroup.class)
    private String code;
    @Schema(description = "refresh_token刷新令牌")
    @NotBlank(groups = RefreshGroup.class)
    private String refresh_token;
    @Schema(description = "access_token访问令牌")
    @NotBlank(groups = RevokeGroup.class)
    private String access_token;


    public interface CodeGroup {

    }

    public interface RefreshGroup {

    }

    public interface RevokeGroup {

    }


}
