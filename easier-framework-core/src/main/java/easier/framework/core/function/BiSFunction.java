package easier.framework.core.function;

import java.io.Serializable;
import java.util.function.BiFunction;

@FunctionalInterface
public interface BiSFunction<T, U, R> extends BiFunction<T, U, R>, Serializable {
}
