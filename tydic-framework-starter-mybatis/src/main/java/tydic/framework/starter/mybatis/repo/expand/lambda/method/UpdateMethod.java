package tydic.framework.starter.mybatis.repo.expand.lambda.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.ChainUpdate;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.core.util.StrUtil;

import java.io.Serializable;
import java.util.Collection;

public interface UpdateMethod<T, SELF extends AbstractChainWrapper<T, SFunction<T, ?>, SELF, ?>>
        extends ChainUpdate<T>, ColumnMethod<T>, TypedSelf<SELF> {
    default boolean updateById(Serializable id) {
        SFunction<T, ?> tableIdColumn = this.getTableIdColumnFor("updateById");
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }
        this.self().eq(tableIdColumn, id);
        return this.update();
    }

    default boolean updateByIds(Collection<Serializable> ids) {
        SFunction<T, ?> tableIdColumn = this.getTableColeColumnFor("updateByIds");
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        this.self().in(tableIdColumn, ids);
        return this.update();
    }


    default boolean updateByCode(String code) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumnFor("updateByCode");
        if (StrUtil.isBlank(code)) {
            return false;
        }
        this.self().eq(tableColeColumn, code);
        return this.update();
    }

    default boolean updateByCodes(Collection<String> codes) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumnFor("updateByCodes");
        if (CollUtil.isEmpty(codes)) {
            return false;
        }
        this.self().in(tableColeColumn, codes);
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
        SFunction<T, ?> tableIdColumn = this.getTableIdColumnFor("deleteById");
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }
        this.self().eq(tableIdColumn, id);
        return this.delete();
    }

    default boolean deleteByIds(Collection<Serializable> ids) {
        SFunction<T, ?> tableIdColumn = this.getTableIdColumnFor("deleteByIds");
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        this.self().in(tableIdColumn, ids);
        return this.delete();
    }


    default boolean deleteByCode(String code) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumnFor("deleteByCode");
        if (StrUtil.isBlank(code)) {
            return false;
        }
        this.self().eq(tableColeColumn, code);
        return this.delete();
    }

    default boolean deleteByCodes(Collection<String> codes) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumnFor("deleteByCodes");
        if (CollUtil.isEmpty(codes)) {
            return false;
        }
        this.self().in(tableColeColumn, codes);
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
