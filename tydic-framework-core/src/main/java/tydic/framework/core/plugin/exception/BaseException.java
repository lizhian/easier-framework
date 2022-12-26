package tydic.framework.core.plugin.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import tydic.framework.core.domain.R;

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
