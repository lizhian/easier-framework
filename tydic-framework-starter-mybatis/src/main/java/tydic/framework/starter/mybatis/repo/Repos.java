package tydic.framework.starter.mybatis.repo;

import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import tydic.framework.core.util.SpringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Repos {
    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();


    public static <T> EntityRepo<T> of(Class<T> entityClass) {
        if (CACHE.containsKey(entityClass.getName())) {
            return (EntityRepo<T>) CACHE.get(entityClass.getName());
        }
        synchronized (Repos.class) {
            if (CACHE.containsKey(entityClass.getName())) {
                return (EntityRepo<T>) CACHE.get(entityClass.getName());
            }
            EntityMapper<T> entityMapper = SpringUtil.getBean(new TypeReference<EntityMapper<T>>() {
            });
            if (entityMapper == null) {
                throw new MybatisPlusException("未找到[EntityMapper<" + entityClass.getSimpleName() + ">]");
            }
            EntityRepo<T> entityRepo = new EntityRepo<>(entityMapper);
            CACHE.put(entityClass.getName(), entityRepo);
            return entityRepo;
        }
    }
}


