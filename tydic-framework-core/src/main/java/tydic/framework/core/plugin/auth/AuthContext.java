package tydic.framework.core.plugin.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import tydic.framework.core.plugin.auth.detail.IAuthDetail;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.core.util.StrUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.List;

@ParametersAreNullableByDefault
public class AuthContext {

    @Nonnull
    public static String getAccount() {
        return StpUtil.getLoginId().toString();
    }

    @Nullable
    public static String getAccount(boolean throwable) {
        try {
            return getAccount();
        } catch (Exception exception) {
            if (throwable) {
                throw exception;
            }
            return null;
        }
    }

    public static void mustLogin() {
        String account = getAccount();
        if (StrUtil.isBlank(account)) {
            String loginType = StpUtil.getLoginType();
            throw NotLoginException.newInstance(loginType, NotLoginException.DEFAULT_MESSAGE);
        }
    }

    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    @Nullable
    public static String getTokenValue() {
        return StpUtil.getTokenValue();
    }

    public static boolean isInnerRequest() {
        try {
            SaSameUtil.checkCurrentRequestToken();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAdmin() {
        mustLogin();
        List<String> roleList = StpUtil.getRoleList();
        if (CollUtil.isEmpty(roleList)) {
            return false;
        }
        return roleList.contains("admin");
    }


    public static <T extends IAuthDetail> void setDetail(Class<T> clazz, T t) {
        mustLogin();
        SaSession session = StpUtil.getSession();
        if (clazz == null) {
            return;
        }
        session.set(clazz.getName(), t);
    }

    @Nullable
    public static <T extends IAuthDetail> T getDetail(Class<T> clazz) {
        mustLogin();
        if (clazz == null) {
            return null;
        }
        SaSession session = StpUtil.getSession();
        Object value = session.get(clazz.getName());
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public static void login(String account) {
        SaLoginModel model = SaLoginModel.create()
                                         .setDevice(SpringUtil.getApplicationName())
                                         .build();
        StpUtil.login(account, model);
    }

    public static void login(String account, SaLoginModel model) {
        if (model == null) {
            login(account);
        }
        StpUtil.login(account, model);
    }
}
