package easier.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.core.proxy.TypedSelf;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.expand.lambda.method.ColumnMethod;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/*
 * 查询单条数据
 */
public interface MethodForList<T> extends TypedSelf<Repo<T>>, ColumnMethod<T> {

    /**
     * 查询全表
     */
    @Nonnull
    default List<T> listAll() {
        return this.self().newQuery().list();
    }

    /**
     * 根据主键查询列表
     */
    @Nonnull
    default List<T> listByIds(Collection<Serializable> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<T> list = this.self().getBaseMapper().selectBatchIds(ids);
        return list == null ? new ArrayList<>() : list;
    }

    /**
     * 根据编码查询列表
     */
    @Nonnull
    default List<T> listByCodes(Collection<String> codes) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumn();
        if (CollUtil.isEmpty(codes)) {
            return new ArrayList<>();
        }
        return this.self()
                .newQuery()
                .in(tableColeColumn, codes)
                .list();
    }

    /**
     * 根据字段查询列表
     */
    @Nonnull
    default <V> List<T> listBy(SFunction<T, V> column, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return new ArrayList<>();
        }
        return this.self()
                .newQuery()
                .eq(column, value)
                .list();
    }

    /**
     * 根据字段查询列表
     */
    @Nonnull
    default <V> List<T> listBy(SFunction<T, V> column, Collection<V> values) {
        if (CollUtil.isEmpty(values)) {
            return new ArrayList<>();
        }
        return this.self()
                .newQuery()
                .in(column, values)
                .list();
    }
}
