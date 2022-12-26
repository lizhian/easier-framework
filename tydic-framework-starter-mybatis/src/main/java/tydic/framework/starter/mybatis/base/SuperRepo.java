package tydic.framework.starter.mybatis.base;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import tydic.framework.core.util.SpringUtil;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

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
    @Setter(AccessLevel.PROTECTED)
    private MPJBaseMapper<T> mapper;

    protected MPJBaseMapper<T> getMapper() {
        if (this.mapper != null) {
            return this.mapper;
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
    }

    protected Class<?> getMapperClass() {
        return this.getMapper().getClass();
    }

    /**
     * 批量执行
     */
    protected final <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        Log log = LogFactory.getLog(this.getClass());
        return SqlHelper.executeBatch(this.getEntityClass(), log, list, batchSize, consumer);
    }

    /**
     * 新建查询
     */
    public final LambdaQueryChainWrapper<T> newQuery() {
        return new LambdaQueryChainWrapper<>(this.getMapper());
    }

    /**
     * 新建关联查询
     */
    public final MPJLambdaWrapper<T> newJoinQuery() {
        return new MPJLambdaWrapper<>(this.getMapper());
    }

    /**
     * 新建更新
     */
    public final LambdaUpdateChainWrapper<T> newUpdate() {
        return new LambdaUpdateChainWrapper<>(this.getEntityClass(), this.getMapper());
    }

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

    /**
     * 新增
     */
    public final boolean add(T entity) {
        if (entity == null) {
            return false;
        }
        return SqlHelper.retBool(this.getMapper().insert(entity));
    }

    /**
     * 批量新增
     */
    public final boolean addBatch(Collection<T> entityList) {
        return this.addBatch(entityList, this.getDefaultBatchSize());
    }

    /**
     * 批量新增
     */
    public final boolean addBatch(Collection<T> entityList, int batchSize) {
        if (CollUtil.isEmpty(entityList)) {
            return false;
        }
        String sqlStatement = SqlHelper.getSqlStatement(this.getMapperClass(), SqlMethod.INSERT_ONE);
        return this.executeBatch(
                entityList
                , batchSize
                , (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity)
        );
    }
}
