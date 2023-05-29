package tydic.framework.starter.mybatis.repo;

import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.github.yulichang.base.MPJBaseMapper;
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
            MPJBaseMapper<T> entityMapper = SpringUtil.getBean(new TypeReference<MPJBaseMapper<T>>() {
            });
            if (entityMapper == null) {
                throw new MybatisPlusException("未找到[MPJBaseMapper<" + entityClass.getSimpleName() + ">]");
            }
            EntityRepo<T> entityRepo = new EntityRepo<>(entityMapper);
            CACHE.put(entityClass.getName(), entityRepo);
            return entityRepo;
        }
    }
}


