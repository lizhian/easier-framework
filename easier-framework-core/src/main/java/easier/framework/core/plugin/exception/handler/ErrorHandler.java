package easier.framework.core.plugin.exception.handler;

import lombok.Getter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;

import javax.annotation.Nonnull;
import java.util.function.Function;

@Getter
public class ErrorHandler<T extends Throwable> {
    /**
     * 异常类型
     */
    private final Class<T> clazz;
    /**
     * 优先级
     */
    private int order = Ordered.LOWEST_PRECEDENCE;
    /**
     * 响应状态
     */
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    /**
     * 以Json形式返回
     */
    private Function<Throwable, Object> responseBody;

    /**
     * 以重定向方式返回
     */
    private Function<Throwable, String> redirect;

    public ErrorHandler(@Nonnull Class<T> clazz) {
        this.clazz = clazz;
    }

    public ErrorHandler<T> order(int order) {
        this.order = order;
        return this;
    }

    public ErrorHandler<T> httpStatus(@Nonnull HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public void responseBody(Function<T, Object> responseBody) {
        this.responseBody = (Function<Throwable, Object>) responseBody;
    }

    public void redirect(Function<T, String> redirect) {
        this.redirect = (Function<Throwable, String>) redirect;
    }
}