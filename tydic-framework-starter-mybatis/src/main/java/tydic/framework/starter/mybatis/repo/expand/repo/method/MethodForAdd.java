package tydic.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.Repo;
import tydic.framework.starter.mybatis.util.MybatisPlusUtil;

import java.util.Collection;


/*
 * 新增方法
 */
public interface MethodForAdd<T> extends TypedSelf<Repo<T>> {

    /**
     * 新增
     */
    default boolean add(T entity) {
        if (entity == null) {
            return false;
        }
        MybatisPlusUtil.preInsert(entity);
        int result = this.self().getBaseMapper().insert(entity);
        return SqlHelper.retBool(result);
    }

    /**
     * 批量新增
     */
    default boolean addBatch(Collection<T> entityList) {
        int defaultBatchSize = this.self().getDefaultBatchSize();
        return this.addBatch(entityList, defaultBatchSize);
    }

    /**
     * 批量新增
     */
    default boolean addBatch(Collection<T> list, int batchSize) {
        if (CollUtil.isEmpty(list)) {
            return false;
        }
        list.forEach(MybatisPlusUtil::preInsert);
        Repo<T> repo = this.self();
        Class<?> mapperClass = repo.getMapperClass();
        String sqlStatement = SqlHelper.getSqlStatement(mapperClass, SqlMethod.INSERT_ONE);
        return repo.executeBatch(
                list,
                batchSize,
                (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity)
        );
    }

}
