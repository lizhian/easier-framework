package easier.framework.starter.mybatis.repo.holder;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.tangzc.mpe.bind.Binder;
import easier.framework.starter.mybatis.repo.Repo;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class BindHolder<T> {
    private final Repo<T> repo;
    private List<SFunction<T, ?>> bindFields;
    /**
     * 根据主键查询
     */
    @Nullable
    public T getById(Serializable id) {
        T entity = this.repo.getById(id);
        Binder.bindOn(entity, this.bindFields);
        return entity;
    }
    /**
     * 根据编码查询
     */
    @Nullable
    public T getByCode(String code) {
        T entity = this.repo.getByCode(code);
        Binder.bindOn(entity, this.bindFields);
        return entity;
    }
    /**
     * 根据字段查询单条数据
     */
    @Nullable
    public <V> T anyBy(SFunction<T, V> column, V value) {
        T entity = this.repo.anyBy(column, value);
        Binder.bindOn(entity, this.bindFields);
        return entity;
    }

    /**
     * 查询全表
     */
    @Nonnull
    public List<T> listAll() {
        List<T> list = this.repo.listAll();
        Binder.bindOn(list, this.bindFields);
        return list;
    }

    /**
     * 根据主键查询
     */
    @Nonnull
    public List<T> listByIds(Collection<Serializable> ids) {
        List<T> list = this.repo.listByIds(ids);
        Binder.bindOn(list, this.bindFields);
        return list;
    }

    /**
     * 根据编码查询
     */
    @Nonnull
    public List<T> listByCodes(Collection<String> codes) {
        List<T> list = this.repo.listByCodes(codes);
        Binder.bindOn(list, this.bindFields);
        return list;
    }

    /**
     * 根据字段查询列表
     */
    @Nonnull
    public <V> List<T> listBy(SFunction<T, V> column, V value) {
        List<T> list = this.repo.listBy(column, value);
        Binder.bindOn(list, this.bindFields);
        return list;
    }

    /**
     * 根据字段查询列表
     */
    @Nonnull
    public <V> List<T> listBy(SFunction<T, V> column, Collection<V> values) {
        List<T> list = this.repo.listBy(column, values);
        Binder.bindOn(list, this.bindFields);
        return list;
    }
}