package easier.framework.starter.auth.template;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.StpInterface;

public interface EasierAuthTemplate extends StpInterface, SaTokenListener {
    String tokenToAccount(String tokenValue);
}
