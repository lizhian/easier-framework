package easier.framework.starter.mybatis.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import easier.framework.starter.mybatis.repo.IRepo;
import easier.framework.starter.mybatis.util.MybatisPlusUtil;

import java.util.Collection;


/*
 * 查询单条数据
 */
public interface MethodForUpdate<T> extends IRepo<T> {

    /**
     * 修改实体数据
     */
    default boolean update(T entity) {
        if (entity == null) {
            return false;
        }
        MybatisPlusUtil.preUpdate(entity);
        int update = this.repo().getBaseMapper().updateById(entity);
        if (update > 0) {
            MybatisPlusUtil.publishUpdateEvent(entity);
        }
        return SqlHelper.retBool(update);
    }

    /**
     * 批量修改
     */
    default boolean updateBatch(Collection<T> entityList) {
        return this.updateBatch(entityList, this.repo().getDefaultBatchSize());
    }

    /**
     * 批量修改
     */
    default boolean updateBatch(Collection<T> list, int batchSize) {
        if (CollUtil.isEmpty(list)) {
            return false;
        }
        list.forEach(MybatisPlusUtil::preUpdate);
        /*Repo<T> repo = this.self();
        String sqlStatement = SqlHelper.getSqlStatement(repo.getMapperClass(), SqlMethod.UPDATE_BY_ID);
        boolean result = repo.executeBatch(
                list,
                batchSize,
                (sqlSession, entity) -> {
                    MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(sqlStatement, param);
                }
        );
        if (result) {
            MybatisPlusUtil.publishUpdateEvent(list);
        }*/
        return true;
    }

}
