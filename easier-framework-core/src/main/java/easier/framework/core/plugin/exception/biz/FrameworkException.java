package easier.framework.core.plugin.exception.biz;

import easier.framework.core.domain.R;
import easier.framework.core.plugin.exception.BaseException;
import easier.framework.core.util.StrUtil;
import lombok.Setter;
import lombok.experimental.StandardException;


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
