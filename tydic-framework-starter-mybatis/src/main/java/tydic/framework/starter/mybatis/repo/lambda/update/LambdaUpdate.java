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
package tydic.framework.starter.mybatis.repo.lambda.update;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.ChainUpdate;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.Getter;
import tydic.framework.starter.mybatis.repo.ColumnSFunction;
import tydic.framework.starter.mybatis.repo.expand.lambda.method.UpdateMethod;
import tydic.framework.starter.mybatis.repo.expand.lambda.method.WhenMethod;
import tydic.framework.starter.mybatis.util.MybatisPlusUtil;

import java.util.List;
import java.util.Map;

public final class LambdaUpdate<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaUpdate<T>, LambdaUpdateWrapper<T>>
        implements ChainUpdate<T>, Update<LambdaUpdate<T>, SFunction<T, ?>>
        , WhenMethod<T, LambdaUpdate<T>>
        , UpdateMethod<T, LambdaUpdate<T>> {

    private final BaseMapper<T> baseMapper;
    @Getter
    private final Class<T> entityClass;

    public LambdaUpdate(BaseMapper<T> baseMapper) {
        super();
        Class<?> clazz = ReflectionKit.getSuperClassGenericType(baseMapper.getClass(), BaseMapper.class, 0);
        this.baseMapper = baseMapper;
        this.entityClass = (Class<T>) clazz;
        super.wrapperChildren = new LambdaUpdateWrapper<T>() {
            @Override
            protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
                if (column instanceof ColumnSFunction) {
                    ColumnSFunction columnSFunction = (ColumnSFunction) column;
                    ColumnCache cache = columnSFunction.toColumnCache();
                    return onlyColumn ? cache.getColumn() : cache.getColumnSelect();
                }
                return this.columnToString(column, onlyColumn);
            }
        };
    }

    @Override
    public LambdaUpdate<T> set(boolean condition, SFunction<T, ?> column, Object val, String mapping) {
        this.wrapperChildren.set(condition, column, val, mapping);
        return this.typedThis;
    }

    @Override
    public LambdaUpdate<T> setSql(boolean condition, String sql) {
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

    private boolean relatedUpdate;

    public final LambdaUpdate<T> relatedUpdate() {
        this.relatedUpdate = true;
        return this;
    }

    @Override
    public boolean update() {
        Map<SFunction, Object> updateSets = MybatisPlusUtil.tryUpdateSets(this);
        int update = this.getBaseMapper().update(null, this.getWrapper());
        if (update > 0 && this.relatedUpdate) {
            MybatisPlusUtil.relatedUpdate(this, updateSets);
        }
        return SqlHelper.retBool(update);
    }


    @Override
    public boolean update(T entity) {
        throw new MybatisPlusException("【LambdaUpdate】不允许使用【update(T entity)】方法");
    }


    private boolean relatedDelete;

    private List<Class<?>> relatedDeleteEntityClasses;

    public final LambdaUpdate<T> relatedDelete(Class<?>... classes) {
        this.relatedDelete = true;
        this.relatedDeleteEntityClasses = CollUtil.newArrayList(classes);
        return this;
    }

    @Override
    public boolean remove() {
        MybatisPlusUtil.tryUpdateSets(this);
        int delete = this.getBaseMapper().delete(this.getWrapper());
        return SqlHelper.retBool(delete);
    }
}
