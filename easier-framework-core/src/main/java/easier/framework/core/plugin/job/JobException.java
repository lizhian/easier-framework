package easier.framework.core.plugin.job;

import cn.hutool.core.util.StrUtil;
import easier.framework.core.plugin.exception.BaseException;
import lombok.experimental.StandardException;

@StandardException
public class JobException extends BaseException {
    public static JobException of(String template, Object... params) {
        return new JobException(StrUtil.format(template, params));
    }

}
