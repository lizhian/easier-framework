package tydic.framework.core.plugin.exception.biz;

import lombok.experimental.StandardException;
import tydic.framework.core.plugin.exception.BaseException;
import tydic.framework.core.util.StrUtil;

/**
 * 业务异常 打印异常信息
 */
@StandardException
public class BizErrorException extends BaseException {
    public static BizErrorException of(String message, Object... params) {
        return new BizErrorException(StrUtil.format(message, params));
    }
}
