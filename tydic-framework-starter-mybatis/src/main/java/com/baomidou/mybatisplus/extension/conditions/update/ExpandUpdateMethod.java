package com.baomidou.mybatisplus.extension.conditions.update;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import tydic.framework.core.plugin.mybatis.MybatisPlusEntity;
import tydic.framework.core.proxy.TypedSelf;

import java.util.HashMap;
import java.util.Map;

/**
 * 拓展更新方法
 */
public interface ExpandUpdateMethod<T> extends ChainUpdate<T>
        , TypedSelf<LambdaUpdateChainWrapper<T>> {

    void afterUpdate(Map<SFunction, Object> updateSets, int updateSize);

    void beforeRemove();

    void afterRemove();

    /**
     * 更新数据
     *
     * @return 是否成功
     */
    @Override
    default boolean update() {
        Map<SFunction, Object> updateSets = this.tryUpdateSets();
        int updateSize = this.getBaseMapper().update(null, this.getWrapper());
        this.afterUpdate(updateSets, updateSize);
        return SqlHelper.retBool(updateSize);

    }


    /**
     * 更新数据
     *
     * @param entity 实体类
     * @return 是否成功
     */
    @Override
    default boolean update(T entity) {
        throw new MybatisPlusException("【LambdaUpdateChainWrapper】不允许使用【update(T entity)】方法");
    }

    /**
     * 删除数据
     *
     * @return 是否成功
     */
    @Override
    default boolean remove() {
        this.tryUpdateSets();
        this.beforeRemove();
        boolean remove = SqlHelper.retBool(this.getBaseMapper().delete(this.getWrapper()));
        if (remove) {
            this.afterRemove();
        }
        return remove;
    }


    private Map<SFunction, Object> tryUpdateSets() {
        try {
            Class<T> entityClass = this.self().getWrapper().getEntityClass();
            T t = ReflectUtil.newInstanceIfPossible(entityClass);
            if (t instanceof MybatisPlusEntity mybatisPlusEntity) {
                Map<SFunction, Object> updateSets = new HashMap<>();
                mybatisPlusEntity.preLambdaUpdate(updateSets);
                for (SFunction column : updateSets.keySet()) {
                    Object value = updateSets.get(column);
                    this.self().set(column, value);
                }
                return updateSets;
            }
            return new HashMap<>();
        } catch (Exception ignored) {
            return new HashMap<>();
        }
    }
}
