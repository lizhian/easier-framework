package easier.framework.test.enums;

import easier.framework.core.plugin.dict.Dict;
import easier.framework.core.plugin.enums.EnumDesc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Dict(code = "oauth2_response_type", name = "oauth2授权类型")
public enum Oauth2GranType {

    authorization_code("通过code获取access_token"),
    refresh_token("通过refresh_token获取access_token");
    @EnumDesc
    private final String desc;
}
