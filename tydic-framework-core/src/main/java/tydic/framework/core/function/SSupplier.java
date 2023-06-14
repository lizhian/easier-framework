package tydic.framework.core.function;

import java.io.Serializable;
import java.util.function.Supplier;

@FunctionalInterface
public interface SSupplier<T> extends Supplier<T>, Serializable {
}
