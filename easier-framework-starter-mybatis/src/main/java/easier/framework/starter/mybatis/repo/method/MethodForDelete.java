package easier.framework.starter.mybatis.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.mybatis.repo.IRepo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/*
 * 查询单条数据
 */
public interface MethodForDelete<T> extends IRepo<T> {

    /**
     * 根据主键删除
     */
    default boolean deleteById(Serializable id) {
        SFunction<T, Serializable> tableId = this.repo().getTableId();
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }

        TableInfo tableInfo = this.repo().getTableInfo();
        if (tableInfo.isWithLogicDelete()) {
            String logicDeleteSql = tableInfo.getLogicDeleteSql(false, false);
            return this.repo()
                    .newUpdate()
                    .setSql(logicDeleteSql)
                    .eq(tableId, id)
                    .update();
        }
        return SqlHelper.retBool(
                this.repo().getBaseMapper().deleteById(id)
        );
    }

    /**
     * 根据主键批量删除
     */
    default boolean deleteByIds(Collection<? extends Serializable> ids) {
        int defaultBatchSize = this.repo().getDefaultBatchSize();
        return this.deleteByIds(ids, defaultBatchSize);
    }

    /**
     * 根据主键批量删除
     */
    default boolean deleteByIds(Collection<? extends Serializable> ids, int batchSize) {
        SFunction<T, Serializable> tableId = this.repo().getTableId();
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        TableInfo tableInfo = this.repo().getTableInfo();
        for (List<? extends Serializable> splitIds : CollUtil.split(ids, batchSize)) {
            if (tableInfo.isWithLogicDelete()) {
                String logicDeleteSql = tableInfo.getLogicDeleteSql(false, false);
                this.repo()
                        .newUpdate()
                        .setSql(logicDeleteSql)
                        .in(tableId, splitIds)
                        .update();
            } else {
                this.repo().getBaseMapper().deleteBatchIds(ids);
            }
        }
        return true;
    }

    /**
     * 根据编码删除
     */
    default boolean deleteByCode(String code) {
        SFunction<T, String> tableCole = this.repo().getTableCole();
        if (StrUtil.isBlank(code)) {
            return false;
        }
        TableInfo tableInfo = this.repo().getTableInfo();
        if (tableInfo.isWithLogicDelete()) {
            String logicDeleteSql = tableInfo.getLogicDeleteSql(false, false);
            return this.repo()
                    .newUpdate()
                    .setSql(logicDeleteSql)
                    .eq(tableCole, code)
                    .update();
        }
        return this.repo()
                .newUpdate()
                .eq(tableCole, code)
                .update();
    }

    /**
     * 根据编码批量删除
     */
    default boolean deleteByCodes(Collection<String> codes) {
        int defaultBatchSize = this.repo().getDefaultBatchSize();
        return this.deleteByCodes(codes, defaultBatchSize);
    }

    /**
     * 根据编码批量删除
     */
    default boolean deleteByCodes(Collection<String> codes, int batchSize) {
        SFunction<T, ?> tableCole = this.repo().getTableCole();
        if (CollUtil.isEmpty(codes)) {
            return false;
        }
        TableInfo tableInfo = this.repo().getTableInfo();
        for (List<String> splitCodes : CollUtil.split(codes, batchSize)) {
            if (tableInfo.isWithLogicDelete()) {
                String logicDeleteSql = tableInfo.getLogicDeleteSql(false, false);
                this.repo()
                        .newUpdate()
                        .setSql(logicDeleteSql)
                        .in(tableCole, splitCodes)
                        .update();
            } else {
                this.repo()
                        .newUpdate()
                        .in(tableCole, splitCodes)
                        .delete();
            }
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
        return this.repo()
                .newUpdate()
                .eq(column, value)
                .delete();
    }

    /**
     * 根据字段批量删除
     */
    default <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values) {
        int defaultBatchSize = this.repo().getDefaultBatchSize();
        return this.deleteBy(column, values, defaultBatchSize);
    }

    /**
     * 根据字段批量删除
     */
    default <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values, int batchSize) {
        if (CollUtil.isEmpty(values)) {
            return false;
        }
        for (List<V> temp : CollUtil.split(values, batchSize)) {
            this.repo()
                    .newUpdate()
                    .in(column, temp)
                    .remove();
        }
        return true;
    }
}
