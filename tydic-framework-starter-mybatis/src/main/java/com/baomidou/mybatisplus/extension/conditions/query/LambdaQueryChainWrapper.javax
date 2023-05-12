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
package com.baomidou.mybatisplus.extension.conditions.query;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import com.tangzc.mpe.bind.Binder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Predicate;

/**
 * 修改原版 {@link LambdaQueryChainWrapper}
 */
@Slf4j
public class LambdaQueryChainWrapper<T>
        extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaQueryChainWrapper<T>, LambdaQueryWrapper<T>>
        implements ChainQuery<T>,
        Query<LambdaQueryChainWrapper<T>, T, SFunction<T, ?>>,
        ExpandCompareMethod<T>,
        ExpandQueryMethod<T> {
    private final BaseMapper<T> baseMapper;

    private boolean bindField;

    @SafeVarargs
    @Override
    public final LambdaQueryChainWrapper<T> select(SFunction<T, ?>... columns) {
        this.wrapperChildren.select(columns);
        return this.typedThis;
    }

    @Override
    public LambdaQueryChainWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        this.wrapperChildren.select(entityClass, predicate);
        return this.typedThis;
    }

    @Override
    public String getSqlSelect() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSelect");
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return this.baseMapper;
    }

    static {
        System.out.println("""
                ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
                ┃ Override LambdaQueryChainWrapper ┃
                ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
                """.stripIndent().trim()
        );
    }

    @Override
    public LambdaQueryChainWrapper<T> self() {
        return this;
    }

    public LambdaQueryChainWrapper(BaseMapper<T> baseMapper) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaQueryWrapper<>();
    }

    private List<SFunction<T, ?>> bindFields;

    @SafeVarargs
    public final LambdaQueryChainWrapper<T> bind(SFunction<T, ?>... fields) {
        this.bindField = true;
        this.bindFields = CollUtil.newArrayList(fields);
        return this;
    }

    @Override
    public void afterQuery(T t) {
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(t);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(t, this.bindFields);
        }
    }

    @Override
    public void afterQuery(List<T> list) {
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(list);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(list, this.bindFields);
        }
    }

    @Override
    public void afterQuery(IPage<T> page) {
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(page);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(page, this.bindFields);
        }
    }
}
