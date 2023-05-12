package tydic.framework.starter.mybatis.repo;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 实体仓库超类
 * 提供基础信息
 */
abstract class SuperRepo<T> {
    @Getter(AccessLevel.PROTECTED)
    private final int defaultBatchSize = 1000;
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Class<T> entityClass = (Class<T>) ReflectionKit.getSuperClassGenericType(this.getClass(), SuperRepo.class, 0);
    //@Setter(AccessLevel.PROTECTED)
    //private EntityMapper<T> entityMapper;

    /*public EntityMapper<T> getEntityMapper() {
        if (this.entityMapper != null) {
            return this.entityMapper;
        }
        MPJBaseMapper mapper = SpringUtil.getBeansOfType(MPJBaseMapper.class)
                                         .values()
                                         .stream()
                                         .filter(it -> {
                                             Class<?> genericType = ReflectionKit.getSuperClassGenericType(it.getClass(), MPJBaseMapper.class, 0);
                                             return this.getEntityClass().equals(genericType);
                                         })
                                         .findAny()
                                         .orElse(null);
        if (mapper == null) {
            throw new MybatisPlusException("未找到[MPJBaseMapper<" + this.entityClass.getSimpleName() + ">]");
        }
        this.mapper = mapper;
        return mapper;
    }*/



    /**
     * 查询全表
     */
    @Nonnull
    public List<T> all() {
        return this.newQuery().list();
    }

    /**
     * 查询总数
     */
    public final long count() {
        return this.newQuery().count();
    }

}
