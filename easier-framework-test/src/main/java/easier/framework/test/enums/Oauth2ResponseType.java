package easier.framework.test.enums;

import easier.framework.core.plugin.dict.Dict;
import easier.framework.core.plugin.enums.EnumDesc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Dict(code = "oauth2_response_type", name = "认证返回方式")
public enum Oauth2ResponseType {
    code("code授权码"),
    token("token访问令牌");
    @EnumDesc
    private final String desc;
}
