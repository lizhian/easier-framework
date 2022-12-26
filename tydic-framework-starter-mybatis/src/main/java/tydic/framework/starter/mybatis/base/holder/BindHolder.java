package tydic.framework.starter.mybatis.base.holder;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.tangzc.mpe.bind.Binder;
import lombok.AllArgsConstructor;
import tydic.framework.starter.mybatis.base.BaseRepo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class BindHolder<T> {
    private final BaseRepo<T> repository;
    private List<SFunction<T, ?>> bindFields;

    @Nullable
    private T bind(T t) {
        if (CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(t);
        } else {
            Binder.bindOn(t, this.bindFields);
        }
        return t;
    }

    @Nonnull
    private List<T> bind(List<T> list) {
        if (CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(list);
        } else {
            Binder.bindOn(list, this.bindFields);
        }
        return list;
    }

    /**
     * 根据主键查询
     */
    @Nullable
    public T getById(Serializable id) {
        return this.bind(this.repository.getById(id));
    }

    /**
     * 根据编码查询
     */
    @Nullable
    public T getByCode(String code) {
        return this.bind(this.repository.getByCode(code));
    }

    /**
     * 根据字段查询单条数据
     */
    @Nullable
    public <V> T anyBy(SFunction<T, V> column, V value) {
        return this.bind(this.repository.anyBy(column, value));
    }

    /**
     * 查询全表
     */
    @Nonnull
    public List<T> all() {
        return this.bind(this.repository.all());
    }

    /**
     * 根据主键查询
     */
    @Nonnull
    public List<T> getByIds(Collection<String> ids) {
        return this.bind(this.repository.getByIds(ids));
    }

    /**
     * 根据编码查询
     */
    @Nonnull
    public List<T> getByCodes(Collection<String> codes) {
        return this.bind(this.repository.getByCodes(codes));
    }

    /**
     * 根据字段查询列表
     */
    @Nonnull
    public <V> List<T> getBy(SFunction<T, V> column, V value) {
        return this.bind(this.repository.getBy(column, value));
    }

    /**
     * 根据字段查询列表
     */
    @Nonnull
    public <V> List<T> getBy(SFunction<T, V> column, Collection<V> values) {
        return this.bind(this.repository.getBy(column, values));
    }
}