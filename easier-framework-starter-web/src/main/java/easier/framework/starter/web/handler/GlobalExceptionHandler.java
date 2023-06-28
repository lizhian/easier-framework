package easier.framework.starter.web.handler;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import easier.framework.core.domain.R;
import easier.framework.core.plugin.exception.BaseException;
import easier.framework.core.plugin.exception.ExceptionHandlerRegister;
import easier.framework.core.util.JacksonUtil;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.ExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.NestedServletException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ExceptionHandler {

    @Override
    @SneakyThrows
    public boolean handleThrowable(HttpServerExchange exchange, ServletRequest request, ServletResponse response, Throwable throwable) {
        throwable = this.getRealThrowable(throwable);
        boolean isPrintStackTrace = true;
        Object responseBody = R.failed();
        int httpStatus = HttpStatus.BAD_REQUEST.value();

        //ResponseStatusException 重写httpStatus
        if (throwable instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
            httpStatus = responseStatusException.getRawStatusCode();
        }

        //BaseException 自定义处理
        if (throwable instanceof BaseException) {
            BaseException baseException = (BaseException) throwable;
            isPrintStackTrace = baseException.isPrintStackTrace();
            responseBody = baseException.getHttpResponseJsonBody();
            httpStatus = baseException.getHttpStatus().value();
        }

        //注册的自定义异常处理
        Class<? extends Throwable> throwableClass = throwable.getClass();
        if (ExceptionHandlerRegister.containsKey(throwableClass)) {
            responseBody = ExceptionHandlerRegister.get(throwableClass).apply(throwable);
            httpStatus = ExceptionHandlerRegister.getHttpStatus(throwableClass);
            isPrintStackTrace = false;
        }
        //是否需要打印日志
        if (isPrintStackTrace) {
            log.error("全局异常处理:{}", throwable.getMessage(), throwable);
        }
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(httpStatus);
            httpServletResponse.setCharacterEncoding(CharsetUtil.UTF_8);
            ServletUtil.write(
                    httpServletResponse
                    , JacksonUtil.toString(responseBody)
                    , ContentType.JSON.getValue()
            );
            return true;
        }
        return false;
    }

    private Throwable getRealThrowable(Throwable throwable) {
        if (throwable instanceof NestedServletException) {
            NestedServletException nestedServletException = (NestedServletException) throwable;
            return this.getRealThrowable(nestedServletException.getCause());
        }
        return throwable;
    }


}
