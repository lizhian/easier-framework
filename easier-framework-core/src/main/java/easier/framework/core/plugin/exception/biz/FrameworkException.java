package easier.framework.core.plugin.exception.biz;

import easier.framework.core.domain.R;
import easier.framework.core.plugin.exception.BaseException;
import easier.framework.core.util.StrUtil;
import lombok.Setter;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;


/**
 * 框架异常
 */
@Setter
@StandardException
public class FrameworkException extends BaseException {

    private Object expandData;

    public static FrameworkException of(Throwable cause, String message, Object... params) {
        return new FrameworkException(StrUtil.format(message, params), cause);
    }

    public static FrameworkException of(String message, Object... params) {
        return new FrameworkException(StrUtil.format(message, params));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public Object getHttpResponseJsonBody() {
        R<Object> failed = R.failed(this.getMessage());
        failed.setExpandData(this.expandData);
        return failed;
    }
}
