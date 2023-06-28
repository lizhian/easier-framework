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
package easier.framework.starter.mybatis.repo.lambda.query;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery;
import com.tangzc.mpe.bind.Binder;
import easier.framework.starter.mybatis.repo.expand.lambda.method.QueryMethod;
import easier.framework.starter.mybatis.repo.expand.lambda.method.WhenMethod;

import java.util.List;
import java.util.function.Predicate;

public class LambdaQuery<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaQuery<T>, LambdaQueryWrapper<T>>
        implements ChainQuery<T>, Query<LambdaQuery<T>, T, SFunction<T, ?>>
        , WhenMethod<T, LambdaQuery<T>>
        , QueryMethod<T> {

    private final BaseMapper<T> baseMapper;

    public LambdaQuery(BaseMapper<T> baseMapper) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaQueryWrapper<>();
    }

    @SafeVarargs
    @Override
    public final LambdaQuery<T> select(SFunction<T, ?>... columns) {
        this.wrapperChildren.select(columns);
        return this.typedThis;
    }

    @Override
    public LambdaQuery<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
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

    @Override
    public Class<T> getEntityClass() {
        return null;
    }

    private boolean bindField;
    private List<SFunction<T, ?>> bindFields;

    @SafeVarargs
    public final LambdaQuery<T> bind(SFunction<T, ?>... fields) {
        this.bindField = true;
        this.bindFields = CollUtil.newArrayList(fields);
        return this;
    }

    @Override
    public List<T> list() {
        List<T> list = this.getBaseMapper().selectList(this.getWrapper());
        if (!this.bindField || CollUtil.isEmpty(list)) {
            return list;
        }
        if (CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(list, this.bindFields);

        } else {
            Binder.bind(list);
        }
        return list;
    }

    @Override
    public T one() {
        T one = this.getBaseMapper().selectOne(this.getWrapper());
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(one);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(one, this.bindFields);
        }
        return one;
    }

    @Override
    public <E extends IPage<T>> E page(E page) {
        E selectPage = this.getBaseMapper().selectPage(page, this.getWrapper());
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(selectPage);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(selectPage, this.bindFields);
        }
        return selectPage;
    }
}
