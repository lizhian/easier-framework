package tydic.user.center.interfaces.cache;

import easier.framework.core.Easier;
import easier.framework.core.plugin.cache.container.CacheContainer;
import easier.framework.core.plugin.dict.DictDetail;

import java.time.Duration;

public class UserCenterCaches {
    public static final CacheContainer<DictDetail> DICT_DETAIL = Easier.Cache
            .newCache(DictDetail.class)
            .keyPrefix(DictDetail.class.getSimpleName())
            .timeToLiveMinutes(2)
            .enableLocalCache(1000, Duration.ofSeconds(2))
            .build();
}
