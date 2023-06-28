package easier.framework.core.proxy;

public interface TypedSelf<T> {
    default T self() {
        return (T) this;
    };
}
