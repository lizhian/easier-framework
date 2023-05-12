package tydic.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.binding.MapperMethod;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.BaseRepo;
import tydic.framework.starter.mybatis.util.MybatisPlusUtil;

import java.util.Collection;


/*
 * 查询单条数据
 */
public interface MethodForUpdate<T, SELF extends BaseRepo<T>> extends TypedSelf<SELF> {

    /**
     * 修改实体数据
     */
    default boolean update(T entity) {
        if (entity == null) {
            return false;
        }
        MybatisPlusUtil.preUpdate(entity);
        int update = this.self().getBaseMapper().updateById(entity);
        if (update > 0) {
            MybatisPlusUtil.publishUpdateEvent(entity);
        }
        return SqlHelper.retBool(update);
    }

    /**
     * 批量修改
     */
    default boolean updateBatch(Collection<T> entityList) {
        return this.updateBatch(entityList, this.self().getDefaultBatchSize());
    }

    /**
     * 批量修改
     */
    default boolean updateBatch(Collection<T> list, int batchSize) {
        if (CollUtil.isEmpty(list)) {
            return false;
        }
        list.forEach(MybatisPlusUtil::preUpdate);
        SELF self = this.self();
        String sqlStatement = SqlHelper.getSqlStatement(self.getMapperClass(), SqlMethod.UPDATE_BY_ID);
        boolean result = self.executeBatch(
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
        }
        return result;
    }
}
