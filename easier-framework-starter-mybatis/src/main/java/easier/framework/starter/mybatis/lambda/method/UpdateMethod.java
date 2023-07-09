package easier.framework.starter.mybatis.lambda.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.ChainUpdate;
import easier.framework.core.proxy.TypedSelf;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.mybatis.repo.IRepo;

import java.io.Serializable;
import java.util.Collection;

public interface UpdateMethod<T, SELF extends AbstractChainWrapper<T, SFunction<T, ?>, SELF, ?>>
        extends ChainUpdate<T>, IRepo<T>, TypedSelf<SELF> {
    default boolean updateById(Serializable id) {
        SFunction<T, ?> tableIdColumn = this.repo().getTableCole();
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }
        this.self().eq(tableIdColumn, id);
        return this.update();
    }

    default boolean updateByIds(Collection<Serializable> ids) {
        SFunction<T, ?> tableIdColumn = this.repo().getTableCole();
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        this.self().in(tableIdColumn, ids);
        return this.update();
    }


    default boolean updateByCode(String code) {
        SFunction<T, ?> tableCole = this.repo().getTableCole();
        if (StrUtil.isBlank(code)) {
            return false;
        }
        this.self().eq(tableCole, code);
        return this.update();
    }

    default boolean updateByCodes(Collection<String> codes) {
        SFunction<T, ?> tableCole = this.repo().getTableCole();
        if (CollUtil.isEmpty(codes)) {
            return false;
        }
        this.self().in(tableCole, codes);
        return this.update();
    }


    default <V> boolean updateBy(SFunction<T, V> column, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        this.self().eq(column, value);
        return this.update();
    }

    default <V> boolean updateBy(SFunction<T, V> column, Collection<V> values) {
        if (CollUtil.isEmpty(values)) {
            return false;
        }
        this.self().in(column, values);
        return this.update();
    }

    default boolean delete() {
        return this.remove();
    }

    default boolean deleteById(Serializable id) {
        SFunction<T, ?> tableIdColumn = this.repo().getTableId();
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }
        this.self().eq(tableIdColumn, id);
        return this.delete();
    }

    default boolean deleteByIds(Collection<Serializable> ids) {
        SFunction<T, ?> tableIdColumn = this.repo().getTableId();
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        this.self().in(tableIdColumn, ids);
        return this.delete();
    }


    default boolean deleteByCode(String code) {
        SFunction<T, ?> tableCole = this.repo().getTableCole();
        if (StrUtil.isBlank(code)) {
            return false;
        }
        this.self().eq(tableCole, code);
        return this.delete();
    }

    default boolean deleteByCodes(Collection<String> codes) {
        SFunction<T, ?> tableCole = this.repo().getTableCole();
        if (CollUtil.isEmpty(codes)) {
            return false;
        }
        this.self().in(tableCole, codes);
        return this.delete();
    }


    default <V> boolean deleteBy(SFunction<T, V> column, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        this.self().eq(column, value);
        return this.delete();
    }

    default <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values) {
        if (CollUtil.isEmpty(values)) {
            return false;
        }
        this.self().in(column, values);
        return this.delete();
    }
}
