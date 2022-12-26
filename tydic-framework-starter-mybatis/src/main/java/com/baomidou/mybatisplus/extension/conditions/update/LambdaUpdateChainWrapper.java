/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.conditions.update;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.tangzc.mpe.base.event.EntityUpdateEvent;
import tydic.framework.core.domain.BaseEntity;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.core.util.TableUtil;
import tydic.framework.starter.mybatis.relatedDelete.EntityDeleteEvent;

import java.util.Date;
import java.util.List;

/**
 * 修改原版 {@link LambdaUpdateChainWrapper}
 */
public class LambdaUpdateChainWrapper<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaUpdateChainWrapper<T>, LambdaUpdateWrapper<T>>
        implements ChainUpdate<T>, Update<LambdaUpdateChainWrapper<T>, SFunction<T, ?>>
        , ExpandCompareMethod<T>
        , ExpandUpdateMethod<T> {

    private final BaseMapper<T> baseMapper;
    private final Class<T> entityClass;

    public LambdaUpdateChainWrapper(Class<T> entityClass, BaseMapper<T> baseMapper) {
        super();
        this.entityClass = entityClass;
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaUpdateWrapper<>();

    }

    @Override
    public LambdaUpdateChainWrapper<T> set(boolean condition, SFunction<T, ?> column, Object val, String mapping) {
        this.wrapperChildren.set(condition, column, val, mapping);
        return this.typedThis;
    }

    @Override
    public LambdaUpdateChainWrapper<T> setSql(boolean condition, String sql) {
        this.wrapperChildren.setSql(condition, sql);
        return this.typedThis;
    }

    @Override
    public String getSqlSet() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSet");
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return this.baseMapper;
    }

    static {
        System.out.println("""
                ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
                ┃ Override LambdaUpdateChainWrapper ┃
                ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
                """.trim()
        );
    }

    @Override
    public LambdaUpdateChainWrapper<T> self() {
        return this;
    }

    private boolean relationDelete;

    private List<T> removeList;

    private List<Class<?>> relatedDeleteEntityClasses;

    @SafeVarargs
    public final LambdaUpdateChainWrapper<T> relatedDelete(Class... classes) {
        this.relationDelete = true;
        this.relatedDeleteEntityClasses = CollUtil.newArrayList(classes);
        return this;
    }

    @Override
    public void afterUpdate(String updateBy, Date updateTime, int updateSize) {
        if (updateSize < 1 || updateTime == null) {
            return;
        }
        String updateTimeColumn = TableUtil.fieldToColumn(this.entityClass, BaseEntity.Fields.updateTime);
        String updateByColumn = TableUtil.fieldToColumn(this.entityClass, BaseEntity.Fields.updateBy);
        ChainWrappers.queryChain(this.baseMapper)
                     .eq(updateTimeColumn, updateTime)
                     .eq(StrUtil.isNotBlank(updateBy), updateByColumn, updateBy)
                     .list()
                     .stream()
                     .map(EntityUpdateEvent::create)
                     .forEach(SpringUtil::publishEvent);
    }


    @Override
    public void beforeRemove() {
        if (this.relationDelete) {
            this.removeList = this.getBaseMapper().selectList(this.getWrapper());
        }
    }

    @Override
    public void afterRemove() {
        if (this.relationDelete && CollUtil.isNotEmpty(this.removeList)) {
            EntityDeleteEvent<T> event = EntityDeleteEvent.create(this.removeList, this.entityClass, this.relatedDeleteEntityClasses);
            SpringUtil.publishEvent(event);
        }
    }
}
