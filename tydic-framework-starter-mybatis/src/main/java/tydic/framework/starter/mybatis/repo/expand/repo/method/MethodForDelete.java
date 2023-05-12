package tydic.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.mybatis.repo.BaseRepo;
import tydic.framework.starter.mybatis.repo.expand.lambda.method.ColumnMethod;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/*
 * 查询单条数据
 */
public interface MethodForDelete<T, SELF extends BaseRepo<T>> extends TypedSelf<SELF>, ColumnMethod<T> {

    /**
     * 根据主键删除
     */
    default boolean deleteById(Serializable id) {
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }
        int result = this.self().getBaseMapper().deleteById(id);
        return SqlHelper.retBool(result);
    }

    /**
     * 根据主键批量删除
     */
    default boolean deleteByIds(Collection<Serializable> ids) {
        int defaultBatchSize = this.self().getDefaultBatchSize();
        return this.deleteByIds(ids, defaultBatchSize);
    }

    /**
     * 根据主键批量删除
     */
    default boolean deleteByIds(Collection<Serializable> ids, int batchSize) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        SELF self = this.self();
        Class<T> entityClass = self.getEntityClass();
        Class<?> mapperClass = self.getMapperClass();
        String sqlStatement = SqlHelper.getSqlStatement(mapperClass, SqlMethod.DELETE_BY_ID);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        return self.executeBatch(ids, batchSize, (sqlSession, e) -> {
            if (tableInfo.isWithLogicDelete()) {
                if (entityClass.isAssignableFrom(e.getClass())) {
                    sqlSession.update(sqlStatement, e);
                } else {
                    T instance = tableInfo.newInstance();
                    tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), e);
                    sqlSession.update(sqlStatement, instance);
                }
            } else {
                sqlSession.update(sqlStatement, e);
            }
        });
    }

    /**
     * 根据编码删除
     */
    default boolean deleteByCode(String code) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumnFor("deleteByCode");
        if (StrUtil.isBlank(code)) {
            return false;
        }
        return this.self().newUpdate().eq(tableColeColumn, code).delete();
    }

    /**
     * 根据编码批量删除
     */
    default boolean deleteByCodes(Collection<String> codes) {
        return this.deleteByCodes(codes, this.self().getDefaultBatchSize());
    }

    /**
     * 根据编码批量删除
     */
    default boolean deleteByCodes(Collection<String> codes, int batchSize) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumnFor("deleteByCodes");
        if (CollUtil.isEmpty(codes)) {
            return false;
        }
        for (List<String> temp : CollUtil.split(codes, batchSize)) {
            this.self().newUpdate()
                    .in(tableColeColumn, temp)
                    .remove();
        }
        return true;
    }

    /**
     * 根据字段删除
     */
    default <V> boolean deleteBy(SFunction<T, V> column, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.self()
                .newUpdate()
                .eq(column, value)
                .remove();
    }

    /**
     * 根据字段批量删除
     */
    default <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values) {
        return this.deleteBy(column, values, this.self().getDefaultBatchSize());
    }

    /**
     * 根据字段批量删除
     */
    default <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values, int batchSize) {
        if (CollUtil.isEmpty(values)) {
            return false;
        }
        for (List<V> temp : CollUtil.split(values, batchSize)) {
            this.self().newUpdate()
                    .in(column, temp)
                    .remove();
        }
        return true;
    }
}