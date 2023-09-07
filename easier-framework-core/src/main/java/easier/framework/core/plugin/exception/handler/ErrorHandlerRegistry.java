package easier.framework.core.plugin.exception.handler;


import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ErrorHandlerRegistry {

    private final Map<Class<? extends Throwable>, List<ErrorHandler<? extends Throwable>>> handlers = new ConcurrentHashMap<>();

    public <T extends Throwable> ErrorHandler<T> of(Class<T> clazz) {
        ErrorHandler<T> errorHandler = new ErrorHandler<>(clazz);
        if (this.handlers.containsKey(clazz)) {
            this.handlers.get(clazz).add(errorHandler);
        } else {
            this.handlers.put(clazz, CollUtil.newArrayList(errorHandler));
        }
        log.info("注册异常处理器: {} {}", clazz.getSimpleName(), this.handlers.get(clazz).size());
        return errorHandler;
    }

    public boolean containsHandler(Class<? extends Throwable> throwableClass) {
        return this.handlers.containsKey(throwableClass);
    }

    public ErrorHandler<? extends Throwable> getHandler(Class<? extends Throwable> throwableClass) {
        return this.handlers.get(throwableClass)
                .stream()
                .min(Comparator.comparingInt(ErrorHandler::getOrder))
                .orElse(null);
    }
}
