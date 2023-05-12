package tydic.framework.starter.mybatis.repo;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.github.yulichang.base.MPJBaseMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tydic.framework.starter.mybatis.repo.expand.repo.method.*;

/**
 * 实体仓库
 * 提供完整的CURD功能
 */
@Slf4j
public abstract class BaseRepo<T>
        implements MethodForAdd<T, BaseRepo<T>>
        , MethodForBatch<T, BaseRepo<T>>
        , MethodForCount<T, BaseRepo<T>>
        , MethodForDelete<T, BaseRepo<T>>
        , MethodForExists<T, BaseRepo<T>>
        , MethodForGet<T, BaseRepo<T>>
        , MethodForLambda<T, BaseRepo<T>>
        , MethodForList<T, BaseRepo<T>>
        , MethodForUnique<T, BaseRepo<T>>
        , MethodForUpdate<T, BaseRepo<T>> {
    @Getter
    private final int defaultBatchSize = 1000;
    @Getter
    private final Class<T> entityClass = (Class<T>) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseRepo.class, 0);
    @Getter
    private final MPJBaseMapper<T> baseMapper;

    protected BaseRepo(MPJBaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public Class<?> getMapperClass() {
        return this.getBaseMapper().getClass();
    }
}
