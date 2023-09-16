package easier.framework.starter.auth.template;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.listener.SaTokenListenerForLog;
import cn.dev33.satoken.stp.StpUtil;
import easier.framework.core.plugin.auth.AuthContext;
import easier.framework.core.plugin.auth.detail.BaseAuthDetail;
import easier.framework.core.util.StrUtil;
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
        BaseAuthDetail detail = AuthContext.getDetail(BaseAuthDetail.class);
        return Optional.ofNullable(detail)
                .map(BaseAuthDetail::getPermissions)
                .orElseGet(ArrayList::new);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        BaseAuthDetail detail = AuthContext.getDetail(BaseAuthDetail.class);
        return Optional.ofNullable(detail)
                .map(BaseAuthDetail::getRoles)
                .orElseGet(ArrayList::new);
    }

    @Override
    public void tryLogin(String token) {
        if (StrUtil.isBlank(token)) {
            return;
        }
        Object loginId = StpUtil.getLoginIdByToken(token);
        if (loginId == null) {
            return;
        }
        AuthContext.login(loginId.toString());
        AuthContext.setDetail(BaseAuthDetail.builder().build());
    }
}
