package easier.framework.core.plugin.exception;

import easier.framework.core.domain.R;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

/**
 * 定义异常规范
 */
@StandardException
public abstract class BaseException extends RuntimeException {

    /**
     * 是否需要打印异常日志信息
     */
    public boolean isPrintStackTrace() {
        return true;
    }

    /**
     * http响应状态
     */
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * 响应体
     */
    public Object getHttpResponseJsonBody() {
        return R.failed(this.getMessage());
    }
}
