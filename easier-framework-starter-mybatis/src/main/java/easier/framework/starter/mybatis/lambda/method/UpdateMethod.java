package easier.framework.starter.mybatis.lambda.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.core.proxy.TypedSelf;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.mybatis.lambda.update.LambdaUpdate;

import java.io.Serializable;
import java.util.Collection;

public interface UpdateMethod<T> extends TypedSelf<LambdaUpdate<T>> {
    default boolean updateById(Serializable id) {
        SFunction<T, ?> tableId = this.self().repo().getTableId();
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }
        return this.self()
                .eq(tableId, id)
                .update();
    }

    default boolean updateByIds(Collection<Serializable> ids) {
        SFunction<T, ?> tableId = this.self().repo().getTableId();
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        return this.self()
                .in(tableId, ids)
                .update();
    }


    default boolean updateByCode(String code) {
        SFunction<T, String> tableCole = this.self().repo().getTableCole();
        if (StrUtil.isBlank(code)) {
            return false;
        }
        return this.self()
                .eq(tableCole, code)
                .update();
    }

    default boolean updateByCodes(Collection<String> codes) {
        SFunction<T, String> tableCole = this.self().repo().getTableCole();
        if (CollUtil.isEmpty(codes)) {
            return false;
        }
        return this.self()
                .in(tableCole, codes)
                .update();
    }


    default <V> boolean updateBy(SFunction<T, V> column, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.self()
                .eq(column, value)
                .update();
    }

    default <V> boolean updateBy(SFunction<T, V> column, Collection<V> values) {
        if (CollUtil.isEmpty(values)) {
            return false;
        }
        return this.self()
                .in(column, values)
                .update();
    }

    default boolean delete() {
        return this.self().remove();
    }

    default boolean deleteById(Serializable id) {
        SFunction<T, ?> tableId = this.self().repo().getTableId();
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }
        return this.self()
                .eq(tableId, id)
                .delete();
    }

    default boolean deleteByIds(Collection<Serializable> ids) {
        SFunction<T, ?> tableId = this.self().repo().getTableId();
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        return this.self()
                .in(tableId, ids)
                .delete();
    }


    default boolean deleteByCode(String code) {
        SFunction<T, ?> tableCole = this.self().repo().getTableCole();
        if (StrUtil.isBlank(code)) {
            return false;
        }
        return this.self()
                .eq(tableCole, code)
                .delete();
    }

    default boolean deleteByCodes(Collection<String> codes) {
        SFunction<T, ?> tableCole = this.self().repo().getTableCole();
        if (CollUtil.isEmpty(codes)) {
            return false;
        }
        return this.self()
                .in(tableCole, codes)
                .delete();
    }


    default <V> boolean deleteBy(SFunction<T, V> column, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.self()
                .eq(column, value)
                .delete();
    }

    default <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values) {
        if (CollUtil.isEmpty(values)) {
            return false;
        }
        return this.self()
                .in(column, values)
                .delete();
    }
}
