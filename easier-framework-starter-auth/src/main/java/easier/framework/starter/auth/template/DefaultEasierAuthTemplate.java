package easier.framework.starter.auth.template;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.listener.SaTokenListenerForLog;
import cn.dev33.satoken.stp.StpUtil;
import easier.framework.core.Easier;
import easier.framework.core.plugin.auth.detail.SimpleAuthDetail;
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
        SimpleAuthDetail detail = Easier.Auth.getDetail(SimpleAuthDetail.class);
        return Optional.ofNullable(detail)
                .map(SimpleAuthDetail::getPermissions)
                .orElseGet(ArrayList::new);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        SimpleAuthDetail detail = Easier.Auth.getDetail(SimpleAuthDetail.class);
        return Optional.ofNullable(detail)
                .map(SimpleAuthDetail::getRoles)
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
        Easier.Auth.login(loginId.toString());
        Easier.Auth.setDetail(SimpleAuthDetail.builder().build());
    }
}
