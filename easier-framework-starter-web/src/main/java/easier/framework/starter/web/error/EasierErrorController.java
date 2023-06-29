package easier.framework.starter.web.error;

import easier.framework.starter.web.handler.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class EasierErrorController extends AbstractErrorController {

    private final ErrorAttributes errorAttributes;

    public EasierErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
        this.errorAttributes = errorAttributes;
    }


    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<String> error(HttpServletRequest request, ServletResponse response) {
        WebRequest webRequest = new ServletWebRequest(request);
        Throwable error = this.errorAttributes.getError(webRequest);
        HttpStatus status = getStatus(request);
        if (error == null && HttpStatus.NOT_FOUND.equals(status)) {
            return ResponseEntity.status(status).build();
        }
        GlobalExceptionHandler.INSTANCE.handleThrowable(null, request, response, error);
        return null;
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> mediaTypeNotAcceptable(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        return ResponseEntity.status(status).build();
    }
}
