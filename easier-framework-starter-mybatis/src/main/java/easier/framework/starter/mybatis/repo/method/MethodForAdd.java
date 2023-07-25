package easier.framework.starter.mybatis.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import easier.framework.starter.mybatis.repo.IRepo;
import easier.framework.starter.mybatis.util.MybatisPlusUtil;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.util.Collection;


/*
 * 新增方法
 */
public interface MethodForAdd<T> extends IRepo<T> {

    /**
     * 新增
     */
    default boolean add(T entity) {
        if (entity == null) {
            return false;
        }
        MybatisPlusUtil.preInsert(entity);
        BaseMapper<T> baseMapper = this.repo().getBaseMapper();
        return SqlHelper.retBool(baseMapper.insert(entity));
    }

    /**
     * 批量新增
     */
    default boolean addBatch(Collection<T> entityList) {
        int defaultBatchSize = this.repo().getDefaultBatchSize();
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
        Class<T> entityClass = this.repo().getEntityClass();
        String sqlStatement = this.repo().getSqlStatement(SqlMethod.INSERT_ONE);
        Log log = LogFactory.getLog(this.repo().getMapperClass());
        return SqlHelper.executeBatch(
                entityClass, log, list, batchSize
                , (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity)
        );
    }

}
