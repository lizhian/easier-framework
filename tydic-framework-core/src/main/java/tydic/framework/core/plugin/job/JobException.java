package tydic.framework.core.plugin.job;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.StandardException;
import tydic.framework.core.plugin.exception.BaseException;

@StandardException
public class JobException extends BaseException {
    public static JobException of(String template, Object... params) {
        return new JobException(StrUtil.format(template, params));
    }

}
