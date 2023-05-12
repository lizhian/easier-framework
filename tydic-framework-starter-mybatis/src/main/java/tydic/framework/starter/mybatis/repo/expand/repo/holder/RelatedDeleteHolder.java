package tydic.framework.starter.mybatis.repo.expand.repo.holder;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.RequiredArgsConstructor;
import tydic.framework.starter.mybatis.repo.BaseRepo;
import tydic.framework.starter.mybatis.util.MybatisPlusUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class RelatedDeleteHolder<T> {
    private final BaseRepo<T> repo;
    private final List<Class<?>> relatedDeleteClasses;

    /**
     * 根据主键删除
     */
    public boolean deleteById(String id) {
        T entity = this.repo.getById(id);
        boolean deleted = this.repo.deleteById(id);
        if (deleted) {
            MybatisPlusUtil.publishDeleteEvent(entity, this.repo.getEntityClass(), this.relatedDeleteClasses);
        }
        return deleted;
    }

    /**
     * 根据主键批量删除
     */
    public boolean deleteByIds(Collection<Serializable> ids) {
        List<T> list = this.repo.listByIds(ids);
        boolean deleted = this.repo.deleteByIds(ids);
        if (deleted) {
            MybatisPlusUtil.publishDeleteEvent(list, this.repo.getEntityClass(), this.relatedDeleteClasses);
        }
        return deleted;
    }


    /**
     * 根据主键批量删除
     */
    public boolean deleteByIds(Collection<Serializable> ids, int batchSize) {
        List<T> list = this.repo.listByIds(ids);
        boolean deleted = this.repo.deleteByIds(ids, batchSize);
        if (deleted) {
            MybatisPlusUtil.publishDeleteEvent(list, this.repo.getEntityClass(), this.relatedDeleteClasses);
        }
        return deleted;
    }

    /**
     * 根据编码删除
     */
    public boolean deleteByCode(String code) {
        T entity = this.repo.getByCode(code);
        boolean deleted = this.repo.deleteByCode(code);
        if (deleted) {
            MybatisPlusUtil.publishDeleteEvent(entity, this.repo.getEntityClass(), this.relatedDeleteClasses);
        }
        return deleted;
    }

    /**
     * 根据编码批量删除
     */
    public boolean deleteByCodes(Collection<String> codes) {
        List<T> list = this.repo.listByCodes(codes);
        boolean deleted = this.repo.deleteByCodes(codes);
        if (deleted) {
            MybatisPlusUtil.publishDeleteEvent(list, this.repo.getEntityClass(), this.relatedDeleteClasses);
        }
        return deleted;
    }

    /**
     * 根据编码批量删除
     */
    public boolean deleteByCodes(Collection<String> codes, int batchSize) {
        List<T> list = this.repo.listByCodes(codes);
        boolean deleted = this.repo.deleteByCodes(codes, batchSize);
        if (deleted) {
            MybatisPlusUtil.publishDeleteEvent(list, this.repo.getEntityClass(), this.relatedDeleteClasses);
        }
        return deleted;
    }


    /**
     * 根据字段删除
     */
    public <V> boolean deleteBy(SFunction<T, V> column, V value) {
        List<T> list = this.repo.listBy(column, value);
        boolean deleted = this.repo.deleteBy(column, value);
        if (deleted) {
            MybatisPlusUtil.publishDeleteEvent(list, this.repo.getEntityClass(), this.relatedDeleteClasses);
        }
        return deleted;
    }

    /**
     * 根据字段批量删除
     */
    public <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values) {
        List<T> list = this.repo.listBy(column, values);
        boolean deleted = this.repo.deleteBy(column, values);
        if (deleted) {
            MybatisPlusUtil.publishDeleteEvent(list, this.repo.getEntityClass(), this.relatedDeleteClasses);
        }
        return deleted;
    }

    /**
     * 根据字段批量删除
     */
    public <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values, int batchSize) {
        List<T> list = this.repo.listBy(column, values);
        boolean deleted = this.repo.deleteBy(column, values, batchSize);
        if (deleted) {
            MybatisPlusUtil.publishDeleteEvent(list, this.repo.getEntityClass(), this.relatedDeleteClasses);
        }
        return deleted;
    }

}