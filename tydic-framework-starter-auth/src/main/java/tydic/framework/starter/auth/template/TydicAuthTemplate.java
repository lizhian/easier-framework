package tydic.framework.starter.auth.template;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.StpInterface;

public interface TydicAuthTemplate extends StpInterface, SaTokenListener {
    String tokenToAccount(String tokenValue);
}
