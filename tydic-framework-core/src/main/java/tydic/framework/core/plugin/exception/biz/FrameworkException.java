package tydic.framework.core.plugin.exception.biz;

import lombok.Setter;
import lombok.experimental.StandardException;
import tydic.framework.core.domain.R;
import tydic.framework.core.plugin.exception.BaseException;
import tydic.framework.core.util.StrUtil;


/**
 * 业务异常 不打印异常信息
 */
@StandardException
public class FrameworkException extends BaseException {

    @Setter
    private Object expandData;

    public static FrameworkException of(String message, Object... params) {
        return new FrameworkException(StrUtil.format(message, params));
    }

    @Override
    public Object getHttpResponseJsonBody() {
        R<Object> failed = R.failed(this.getMessage());
        failed.setExpandData(this.expandData);
        return failed;
    }
}