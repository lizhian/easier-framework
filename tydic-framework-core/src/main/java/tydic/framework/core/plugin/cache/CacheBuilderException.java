package tydic.framework.core.plugin.cache;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.StandardException;
import tydic.framework.core.plugin.exception.BaseException;

/**
 * 缓存异常
 */
@StandardException
public class CacheBuilderException extends BaseException {

    public static CacheBuilderException of(String template, Object... params) {
        return new CacheBuilderException(StrUtil.format(template, params));
    }

}
