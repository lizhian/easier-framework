package easier.framework.test.qo;

import cn.dev33.satoken.oauth2.model.RequestAuthModel;
import easier.framework.test.enums.Oauth2ResponseType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OAuthAuthorizeQo {

    @Schema(description = "返回类型")
    @NotNull
    private Oauth2ResponseType response_type;

    @Schema(description = "应用主键")
    @NotBlank
    private String client_id;

    @Schema(description = "重定向地址")
    @NotBlank
    private String redirect_uri;

    @Schema(description = "token模式下是否使用?符号返回token,默认重定向为[uri#token=xxx],true=[uri?token=xxx]"
            , allowableValues = {"true", "false"}
    )
    @NotNull
    private boolean question_mark;

    public RequestAuthModel toRequestAuthModel(String account) {
        RequestAuthModel ra = new RequestAuthModel();
        ra.clientId = this.client_id;
        ra.responseType = response_type.name();
        ra.redirectUri = redirect_uri;
        ra.state = "";
        ra.scope = "";
        ra.loginId = account;
        return ra;
    }
}
