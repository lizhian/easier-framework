package easier.framework.core.function;

import java.io.Serializable;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface BiSConsumer<T, U> extends BiConsumer<T, U>, Serializable {
}
