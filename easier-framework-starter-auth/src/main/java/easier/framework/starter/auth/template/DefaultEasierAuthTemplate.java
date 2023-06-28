package easier.framework.starter.auth.template;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.listener.SaTokenListenerForLog;
import cn.dev33.satoken.stp.StpUtil;
import easier.framework.core.plugin.auth.AuthContext;
import easier.framework.core.plugin.auth.detail.SimpleAuthDetail;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DefaultEasierAuthTemplate implements EasierAuthTemplate {

    @Delegate(types = SaTokenListener.class)
    private final SaTokenListenerForLog delegate = new SaTokenListenerForLog();

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        SimpleAuthDetail detail = AuthContext.getDetail(SimpleAuthDetail.class);
        return Optional.ofNullable(detail)
                .map(SimpleAuthDetail::getPermissionList)
                .orElseGet(ArrayList::new);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        SimpleAuthDetail detail = AuthContext.getDetail(SimpleAuthDetail.class);
        return Optional.ofNullable(detail)
                .map(SimpleAuthDetail::getRoleList)
                .orElseGet(ArrayList::new);
    }

    @Override
    public String tokenToAccount(String tokenValue) {
        Object loginId = StpUtil.getLoginIdByToken(tokenValue);
        return Optional.ofNullable(loginId)
                .map(Object::toString)
                .orElse(null);
    }


}
