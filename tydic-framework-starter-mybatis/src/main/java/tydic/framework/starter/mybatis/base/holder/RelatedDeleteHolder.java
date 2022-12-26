package tydic.framework.starter.mybatis.base.holder;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.AllArgsConstructor;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.mybatis.base.BaseRepo;
import tydic.framework.starter.mybatis.relatedDelete.EntityDeleteEvent;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class RelatedDeleteHolder<T> {
    private final BaseRepo<T> repository;
    private List<Class<?>> relatedDeleteClasses;

    private boolean publishEvent(boolean deleted, List<T> list) {
        if (deleted && CollUtil.isNotEmpty(list)) {
            EntityDeleteEvent<T> entityDeleteEvent = EntityDeleteEvent.create(list, this.repository.getEntityClass(), this.relatedDeleteClasses);
            SpringUtil.publishEvent(entityDeleteEvent);
        }
        return deleted;
    }

    /**
     * 根据主键删除
     */
    public boolean deleteById(String id) {
        T entity = this.repository.getById(id);
        boolean deleted = this.repository.deleteById(id);
        return this.publishEvent(deleted, CollUtil.newArrayList(entity));
    }

    /**
     * 根据主键批量删除
     */
    public boolean deleteByIds(Collection<String> ids) {
        List<T> list = this.repository.getByIds(ids);
        boolean deleted = this.repository.deleteByIds(ids);
        return this.publishEvent(deleted, list);
    }


    /**
     * 根据主键批量删除
     */
    public boolean deleteByIds(Collection<String> ids, int batchSize) {
        List<T> list = this.repository.getByIds(ids);
        boolean deleted = this.repository.deleteByIds(ids, batchSize);
        return this.publishEvent(deleted, list);
    }

    /**
     * 根据编码删除
     */
    public boolean deleteByCode(String code) {
        T entity = this.repository.getByCode(code);
        boolean deleted = this.repository.deleteByCode(code);
        return this.publishEvent(deleted, CollUtil.newArrayList(entity));
    }

    /**
     * 根据编码批量删除
     */
    public boolean deleteByCodes(Collection<String> codes) {
        List<T> list = this.repository.getByCodes(codes);
        boolean deleted = this.repository.deleteByCodes(codes);
        return this.publishEvent(deleted, list);
    }

    /**
     * 根据编码批量删除
     */
    public boolean deleteByCodes(Collection<String> codes, int batchSize) {
        List<T> list = this.repository.getByCodes(codes);
        boolean deleted = this.repository.deleteByCodes(codes, batchSize);
        return this.publishEvent(deleted, list);
    }


    /**
     * 根据字段删除
     */
    public <V> boolean deleteBy(SFunction<T, V> column, V value) {
        List<T> list = this.repository.getBy(column, value);
        boolean deleted = this.repository.deleteBy(column, value);
        return this.publishEvent(deleted, list);
    }

    /**
     * 根据字段批量删除
     */
    public <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values) {
        List<T> list = this.repository.getBy(column, values);
        boolean deleted = this.repository.deleteBy(column, values);
        return this.publishEvent(deleted, list);
    }

    /**
     * 根据字段批量删除
     */
    public <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values, int batchSize) {
        List<T> list = this.repository.getBy(column, values);
        boolean deleted = this.repository.deleteBy(column, values, batchSize);
        return this.publishEvent(deleted, list);
    }

}