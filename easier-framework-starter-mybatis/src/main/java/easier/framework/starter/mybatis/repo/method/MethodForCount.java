package easier.framework.starter.mybatis.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.starter.mybatis.repo.IRepo;

import java.io.Serializable;
import java.util.Collection;


/*
 * 查询单条数据
 */
public interface MethodForCount<T> extends IRepo<T> {
    /**
     * 查询总数
     */
    default long count() {
        return this.repo()
                .newQuery()
                .count();
    }

    /**
     * 查询数量
     */
    default long countByIds(Collection<Serializable> ids) {
        SFunction<T, ?> tableId = this.repo().getTableId();
        if (CollUtil.isEmpty(ids)) {
            return 0;
        }
        return this.repo()
                .newQuery()
                .in(tableId, ids)
                .count();
    }

    /**
     * 查询数量
     */
    default long countByCodes(Collection<String> codes) {
        SFunction<T, ?> tableCole = this.repo().getTableCole();
        if (CollUtil.isEmpty(codes)) {
            return 0;
        }
        return this.repo()
                .newQuery()
                .in(tableCole, codes)
                .count();
    }

    /**
     * 查询数量
     */
    default <V> long countBy(SFunction<T, V> column, V value) {
        if (value == null) {
            return 0;
        }
        return this.repo()
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
        return this.repo()
                .newQuery()
                .in(column, values)
                .count();
    }

}
