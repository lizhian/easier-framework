package easier.framework.starter.web.error;

import easier.framework.core.domain.R;
import easier.framework.core.plugin.exception.BaseException;
import easier.framework.core.plugin.exception.handler.ErrorHandler;
import easier.framework.core.plugin.exception.handler.ErrorHandlerRegistry;
import easier.framework.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@Slf4j
public class EasierErrorController extends AbstractErrorController {

    private final ErrorAttributes errorAttributes;
    private final ErrorHandlerRegistry errorHandlerRegistry;

    public EasierErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers, ErrorHandlerRegistry errorHandlerRegistry) {
        super(errorAttributes, errorViewResolvers);
        this.errorAttributes = errorAttributes;
        this.errorHandlerRegistry = errorHandlerRegistry;
    }


    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<Object> error(HttpServletRequest request, HttpServletResponse response) {
        WebRequest webRequest = new ServletWebRequest(request);
        Throwable error = this.errorAttributes.getError(webRequest);
        HttpStatus status = this.getStatus(request);
        if (error == null && HttpStatus.NOT_FOUND.equals(status)) {
            return ResponseEntity.status(status).build();
        }
        return this.handleThrowable(request, response, error);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> mediaTypeNotAcceptable(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        return ResponseEntity.status(status).build();
    }

    @SneakyThrows
    public ResponseEntity<Object> handleThrowable(HttpServletRequest request, HttpServletResponse response, Throwable originalThrowable) {
        Throwable throwable = this.getRealThrowable(originalThrowable);
        boolean isPrintStackTrace = true;
        Object responseBody = R.failed();
        int httpStatus = HttpStatus.BAD_REQUEST.value();
        String redirect = null;

        // ResponseStatusException 重写httpStatus
        if (throwable instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
            httpStatus = responseStatusException.getRawStatusCode();
        }

        // BaseException 自定义处理
        if (throwable instanceof BaseException) {
            BaseException baseException = (BaseException) throwable;
            isPrintStackTrace = baseException.isPrintStackTrace();
            responseBody = baseException.getHttpResponseJsonBody();
            httpStatus = baseException.getHttpStatus().value();
        }

        // 注册的自定义异常处理
        Class<? extends Throwable> throwableClass = throwable != null ? throwable.getClass() : null;
        if (this.errorHandlerRegistry.containsHandler(throwableClass)) {
            ErrorHandler<? extends Throwable> errorHandler = this.errorHandlerRegistry.getHandler(throwableClass);
            if (errorHandler.getRedirect() != null) {
                redirect = errorHandler.getRedirect().apply(throwable);
            }
            if (errorHandler.getResponseBody() != null) {
                responseBody = errorHandler.getResponseBody().apply(throwable);
            }
            httpStatus = errorHandler.getHttpStatus().value();
            isPrintStackTrace = false;
        }
        // 是否需要打印详细日志
        if (isPrintStackTrace) {
            log.error("全局异常处理: {}", throwable != null ? throwable.getMessage() : null, throwable);
        } else {
            log.error("全局异常处理: {}", throwable != null ? throwable.getMessage() : null);
        }
        if (StrUtil.isNotBlank(redirect)) {
            log.error("全局异常处理结果, 重定向至: {}", redirect);
            response.sendRedirect(redirect);
            return null;
        }
        return ResponseEntity.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody);
    }

    private Throwable getRealThrowable(Throwable throwable) {
        if (throwable instanceof NestedServletException) {
            NestedServletException nestedServletException = (NestedServletException) throwable;
            return this.getRealThrowable(nestedServletException.getCause());
        }
        return throwable;
    }
}
