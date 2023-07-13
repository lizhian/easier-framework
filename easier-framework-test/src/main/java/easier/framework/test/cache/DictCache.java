package easier.framework.test.cache;

import easier.framework.core.plugin.cache.Cache;
import easier.framework.core.plugin.cache.annotation.CacheGet;
import easier.framework.core.plugin.cache.annotation.CacheUpdate;
import easier.framework.core.plugin.cache.annotation.CacheValue;
import easier.framework.core.plugin.cache.enums.LocalCache;
import easier.framework.core.plugin.dict.DictDetail;

import java.util.concurrent.TimeUnit;

/**
 * 字典缓存
 *
 * @author lizhian
 * @date 2023年07月12日
 */
public interface DictCache extends Cache {
    @CacheGet(
            name = "DictDetail"
            , timeToLive = 2
            , timeUnit = TimeUnit.MINUTES
            , localCache = LocalCache.live2s
    )
    DictDetail getDictDetail(String dictCode);

    @CacheUpdate(
            name = "DictDetail"
            , timeToLive = 2
            , timeUnit = TimeUnit.MINUTES
            , localCache = LocalCache.live2s
    )
    boolean updateDictDetail(String dictCode, @CacheValue DictDetail value);
}
