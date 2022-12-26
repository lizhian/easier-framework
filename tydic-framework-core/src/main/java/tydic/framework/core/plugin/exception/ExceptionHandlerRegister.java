package tydic.framework.core.plugin.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import tydic.framework.core.domain.R;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
public class ExceptionHandlerRegister {
    private static final Map<Class<? extends Throwable>, Function<Throwable, Object>> handlers = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Throwable>, HttpStatus> httpStatuses = new ConcurrentHashMap<>();

    static {
        //空指针异常
        register(NullPointerException.class, exception -> {
            //自定义处理
            log.error("系统发生空指针异常", exception);
            return R.failed("系统发生空指针异常,请联系管理员");
        });
        //内存溢出
        register(OutOfMemoryError.class, exception -> {
            //自定义处理
            log.error("系统发生内存溢出", exception);
            return R.failed("系统发生内存溢出,请联系管理员");
        });
    }


    public static <T extends Throwable> void register(Class<T> clazz, HttpStatus httpStatus, Function<T, Object> function) {
        handlers.put(clazz, (Function<Throwable, Object>) function);
        httpStatuses.put(clazz, httpStatus);
    }


    public static <T extends Throwable> void register(Class<T> clazz, Function<T, Object> function) {
        register(clazz, HttpStatus.BAD_REQUEST, function);
    }

    public static boolean containsKey(Class<? extends Throwable> clazz) {
        return handlers.containsKey(clazz);
    }

    public static Function<Throwable, Object> get(Class<? extends Throwable> clazz) {
        return handlers.get(clazz);
    }

    public static int getHttpStatus(Class<? extends Throwable> throwableClass) {
        return httpStatuses.get(throwableClass).value();
    }
}
