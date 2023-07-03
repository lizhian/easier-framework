package easier.framework.starter.mybatis.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Repos {
    private static final Map<Class<?>, Repo<?>> CACHE = new ConcurrentHashMap<>();


    public static <T> Repo<T> of(Class<T> entityClass) {
        Repo<?> repo = CACHE.computeIfAbsent(entityClass, Repo::new);
        return (Repo<T>) repo;
    }
}


