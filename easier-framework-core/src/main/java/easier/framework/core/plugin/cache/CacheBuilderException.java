package easier.framework.core.plugin.cache;

import cn.hutool.core.util.StrUtil;
import easier.framework.core.plugin.exception.BaseException;
import lombok.experimental.StandardException;

/**
 * 缓存异常
 */
@StandardException
@Deprecated
public class CacheBuilderException extends BaseException {

    public static CacheBuilderException of(String template, Object... params) {
        return new CacheBuilderException(StrUtil.format(template, params));
    }

}
