package tydic.user.center.interfaces.cache;

import easier.framework.core.plugin.cache.Caches;
import easier.framework.core.plugin.cache.container.CacheContainer;
import easier.framework.core.plugin.dict.DictDetail;

public class UserCenterCaches {
    public static final CacheContainer<DictDetail> DICT_DETAIL
            = Caches.ofContainer(DictDetail.class)
            .keyPrefix(DictDetail.class.getSimpleName())
            .timeToLiveMinutes(2)
            .localCache2s()
            .build();
}
