package easier.framework.test.cache;

import easier.framework.core.plugin.cache.Caches;
import easier.framework.core.plugin.cache.container.CacheContainer;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.test.qo.LoginRedirectQo;

public class UserCenterCaches {
    public static final CacheContainer<DictDetail> DICT_DETAIL
            = Caches.ofContainer(DictDetail.class)
            .keyPrefix(DictDetail.class.getSimpleName())
            .timeToLiveMinutes(2)
            .localLive2s()
            .build();


    public static final CacheContainer<String> captcha
            = Caches.ofContainer(String.class)
            .keyPrefix("Captcha")
            .timeToLiveSeconds(30)
            .build();

    public static final CacheContainer<LoginRedirectQo> LOGIN_REDIRECT
            = Caches.ofContainer(LoginRedirectQo.class)
            .keyPrefix("LoginRedirect")
            .timeToLiveMinutes(2)
            .build();
}
