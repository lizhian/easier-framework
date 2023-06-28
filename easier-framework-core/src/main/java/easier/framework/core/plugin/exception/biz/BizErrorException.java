package easier.framework.core.plugin.exception.biz;

import easier.framework.core.plugin.exception.BaseException;
import easier.framework.core.util.StrUtil;
import lombok.experimental.StandardException;

/**
 * 业务异常 打印异常信息
 */
@StandardException
public class BizErrorException extends BaseException {
    public static BizErrorException of(String message, Object... params) {
        return new BizErrorException(StrUtil.format(message, params));
    }
}
