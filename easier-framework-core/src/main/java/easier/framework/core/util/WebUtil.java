package easier.framework.core.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class WebUtil {


    public static HttpServletRequest getRequest() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
                return servletRequestAttributes.getRequest();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static HttpServletResponse getResponse() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
                return servletRequestAttributes.getResponse();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static Optional<HttpServletRequest> getRequestOpt() {
        return Optional.ofNullable(getRequest());
    }

    public static Optional<HttpServletResponse> getResponseOpt() {
        return Optional.ofNullable(getResponse());
    }
}
