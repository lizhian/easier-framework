package com.baomidou.mybatisplus.extension.conditions.update;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import tydic.framework.core.domain.BaseEntity;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.mybatis.template.TydicMybatisTemplate;

import java.util.Date;

/**
 * 拓展更新方法
 */
public interface ExpandUpdateMethod<T> extends ChainUpdate<T>
        , TypedSelf<LambdaUpdateChainWrapper<T>> {

    void afterUpdate(String updateBy, Date updateTime, int updateSize);

    void beforeRemove();

    void afterRemove();

    /**
     * 更新数据
     *
     * @return 是否成功
     */
    @Override
    default boolean update() {
        TydicMybatisTemplate template = SpringUtil.getAndCache(TydicMybatisTemplate.class);
        String updateBy = template.currentHandler();
        Date updateTime = DateTime.now().setField(DateField.MILLISECOND, 0);
        try {
            this.updateBaseField(updateBy, updateTime);
        } catch (Exception e) {
            updateBy = null;
            updateTime = null;
        }
        int updateSize = this.getBaseMapper().update(null, this.getWrapper());
        this.afterUpdate(updateBy, updateTime, updateSize);
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
        TydicMybatisTemplate template = SpringUtil.getAndCache(TydicMybatisTemplate.class);
        String updateBy = template.currentHandler();
        Date updateTime = DateTime.now().setField(DateField.MILLISECOND, 0);
        try {
            this.updateBaseField(updateBy, updateTime);
        } catch (Exception ignored) {
        }
        this.beforeRemove();
        boolean remove = SqlHelper.retBool(this.getBaseMapper().delete(this.getWrapper()));
        if (remove) {
            this.afterRemove();
        }
        return remove;
    }

    private void updateBaseField(String updateBy, Date updateTime) {
        SFunction<BaseEntity, Object> updateBySFunction = BaseEntity::getUpdateBy;
        SFunction<BaseEntity, Object> updateTimeSFunction = BaseEntity::getUpdateTime;
        this.self().set((SFunction) updateBySFunction, updateBy);
        this.self().set((SFunction) updateTimeSFunction, updateTime);
    }
}
