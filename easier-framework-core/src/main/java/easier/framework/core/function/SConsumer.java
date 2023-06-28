package easier.framework.core.function;

import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface SConsumer<T> extends Consumer<T>, Serializable {
}
