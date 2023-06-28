package easier.framework.starter.mybatis.repo;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.github.yulichang.base.MPJBaseMapper;
import easier.framework.starter.mybatis.repo.expand.repo.method.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;

import java.util.Arrays;

/**
 * 实体仓库
 * 提供完整的CURD功能
 */
@Slf4j
public final class Repo<T> implements
        MethodForAdd<T>
        , MethodForBatch<T>
        , MethodForCount<T>
        , MethodForDelete<T>
        , MethodForExists<T>
        , MethodForGet<T>
        , MethodForLambda<T>
        , MethodForList<T>
        , MethodForUnique<T>
        , MethodForUpdate<T>
        , MethodForWith<T> {
    @Getter
    private final MPJBaseMapper<T> baseMapper;

    @Getter
    private final Class<T> entityClass;

    @Getter
    private final int defaultBatchSize;

    public Repo(MPJBaseMapper<T> baseMapper) {
        this(baseMapper, 1000);
    }

    public Repo(MPJBaseMapper<T> baseMapper, int defaultBatchSize) {
        Class<?> genericType = ReflectionKit.getSuperClassGenericType(baseMapper.getClass(), MPJBaseMapper.class, 0);
        this.baseMapper = baseMapper;
        this.entityClass = (Class<T>) genericType;
        this.defaultBatchSize = defaultBatchSize;
    }

    public Class<?> getMapperClass() {
        Class<?>[] interfaces = AopProxyUtils.proxiedUserInterfaces(this.getBaseMapper());
        return Arrays.stream(interfaces)
                .filter(MPJBaseMapper.class::isAssignableFrom)
                .findAny().orElse(null);
    }
}
