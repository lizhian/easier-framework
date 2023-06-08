package tydic.framework.starter.mybatis.repo;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.github.yulichang.base.MPJBaseMapper;
import tydic.framework.core.util.SpringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Repos {
    private static final Map<String, Repo<?>> CACHE = new ConcurrentHashMap<>();


    public static <T> Repo<T> of(Class<T> entityClass) {
        if (CACHE.containsKey(entityClass.getName())) {
            Repo<?> value = CACHE.get(entityClass.getName());
            return (Repo<T>) value;
        }
        synchronized (Repos.class) {
            if (CACHE.containsKey(entityClass.getName())) {
                Repo<?> value = CACHE.get(entityClass.getName());
                return (Repo<T>) value;
            }
            MPJBaseMapper<T> mapper = getMapper(entityClass);
            if (mapper == null) {
                throw new MybatisPlusException("未找到[MPJBaseMapper<" + entityClass.getSimpleName() + ">]");
            }
            Repo<T> entityRepo = new Repo<>(mapper);
            CACHE.put(entityClass.getName(), entityRepo);
            return entityRepo;
        }
    }

    public static <T> MPJBaseMapper<T> getMapper(Class<T> entityClass) {
        MPJBaseMapper<?> mapper = SpringUtil.getBeansOfType(MPJBaseMapper.class)
                .values()
                .stream()
                .filter(it -> {
                    Class<?> genericType = ReflectionKit.getSuperClassGenericType(it.getClass(), MPJBaseMapper.class, 0);
                    return genericType.equals(entityClass);
                })
                .findAny()
                .orElse(null);
        if (mapper == null) {
            return null;
        }
        return (MPJBaseMapper<T>) mapper;
    }
}


