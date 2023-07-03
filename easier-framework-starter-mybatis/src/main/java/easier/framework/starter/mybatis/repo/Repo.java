package easier.framework.starter.mybatis.repo;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.tangzc.mpe.base.MapperScanner;
import easier.framework.starter.mybatis.repo.method.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;

import java.util.Arrays;

/**
 * 实体仓库
 * 提供完整的CURD功能
 */
@Slf4j
public final class Repo<T> implements MethodForAdd<T>, MethodForBatch<T>, MethodForCount<T>, MethodForDelete<T>, MethodForExists<T>, MethodForGet<T>, MethodForLambda<T>, MethodForList<T>, MethodForUnique<T>, MethodForUpdate<T>, MethodForWith<T> {

    private final Supplier<BaseMapper<T>> lazyMapper;

    @Getter
    private final Class<T> entityClass;

    @Getter
    private final int defaultBatchSize;

    public Repo(Class<T> entityClass) {
        this(entityClass, 1000);
    }

    public Repo(Class<T> entityClass, int defaultBatchSize) {
        this.entityClass = entityClass;
        this.defaultBatchSize = defaultBatchSize;
        this.lazyMapper = Suppliers.memoize(() -> {
            try {
                return MapperScanner.getMapper(entityClass);
            } catch (Exception e) {
                throw new MybatisPlusException("未找到[BaseMapper<" + entityClass.getSimpleName() + ">]");
            }
        });
    }

    public Class<?> getMapperClass() {
        Class<?>[] interfaces = AopProxyUtils.proxiedUserInterfaces(this.getBaseMapper());
        return Arrays.stream(interfaces)
                .filter(BaseMapper.class::isAssignableFrom)
                .findAny()
                .orElse(null);
    }

    public BaseMapper<T> getBaseMapper() {
        return lazyMapper.get();
    }

    public MPJBaseMapper<T> getMPJBaseMapper() {
        return (MPJBaseMapper<T>) lazyMapper.get();
    }
}
