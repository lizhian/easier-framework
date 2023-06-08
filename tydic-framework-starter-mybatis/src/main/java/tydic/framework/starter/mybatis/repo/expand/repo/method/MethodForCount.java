package tydic.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.Repo;
import tydic.framework.starter.mybatis.repo.expand.lambda.method.ColumnMethod;

import java.io.Serializable;
import java.util.Collection;


/*
 * 查询单条数据
 */
public interface MethodForCount<T> extends ColumnMethod<T>, TypedSelf<Repo<T>> {
    /**
     * 查询总数
     */
    default long count() {
        return this.self().newQuery().count();
    }

    /**
     * 查询数量
     */
    default long countByIds(Collection<Serializable> ids) {
        SFunction<T, ?> tableIdColumn = this.getTableIdColumn();
        if (CollUtil.isEmpty(ids)) {
            return 0;
        }
        return this.self()
                .newQuery()
                .in(tableIdColumn, ids)
                .count();
    }

    /**
     * 查询数量
     */
    default long countByCodes(Collection<String> codes) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumn();
        if (CollUtil.isEmpty(codes)) {
            return 0;
        }
        return this.self()
                .newQuery()
                .in(tableColeColumn, codes)
                .count();
    }

    /**
     * 查询数量
     */
    default <V> long countBy(SFunction<T, V> column, V value) {
        if (value == null) {
            return 0;
        }
        return this.self()
                .newQuery()
                .eq(column, value)
                .count();
    }

    /**
     * 查询数量
     */
    default <V> long countBy(SFunction<T, V> column, Collection<V> values) {
        if (CollUtil.isEmpty(values)) {
            return 0;
        }
        return this.self()
                .newQuery()
                .in(column, values)
                .count();
    }

}
