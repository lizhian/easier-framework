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
package easier.framework.starter.mybatis.lambda.query;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.starter.mybatis.lambda.ColumnSFunction;
import easier.framework.starter.mybatis.lambda.QueryWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class LambdaQuery2<T> implements QueryWrapper<LambdaQuery2<T>, T>


        /*implements ChainQuery<T>
        , Join<LambdaQuery2<T>>
        , Func<LambdaQuery2<T>, SFunction<T, ?>>
        , Compare<LambdaQuery2<T>, SFunction<T, ?>>
        , Query<LambdaQuery2<T>, T, SFunction<T, ?>>
        , Nested<LambdaQueryWrapper<T>, LambdaQuery2<T>>*/ {
    private final LambdaQueryWrapper<T> delegate;

    public LambdaQuery2(Class<T> entityClass) {
        this.delegate = new LambdaQueryWrapper<T>(entityClass) {
            @Override
            protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
                if (column instanceof ColumnSFunction) {
                    ColumnSFunction<T, ?> columnSFunction = (ColumnSFunction<T, ?>) column;
                    ColumnCache cache = columnSFunction.toColumnCache();
                    return onlyColumn ? cache.getColumn() : cache.getColumnSelect();
                }
                return super.columnToString(column, onlyColumn);
            }
        };
    }

    @SafeVarargs
    @Override
    public final LambdaQuery2<T> select(SFunction<T, ?>... columns) {
        this.delegate.select(columns);
        return this;
    }

    @Override
    public LambdaQuery2<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        this.delegate.select(entityClass, predicate);
        return this;
    }

    @Override
    public String getSqlSelect() {
        return this.delegate.getSqlSelect();
    }

    @Override
    public <V> LambdaQuery2<T> allEq(boolean condition, Map<SFunction<T, ?>, V> params, boolean null2IsNull) {
        this.delegate.allEq(condition, params, null2IsNull);
        return this;
    }

    @Override
    public <V> LambdaQuery2<T> allEq(boolean condition, BiPredicate<SFunction<T, ?>, V> filter, Map<SFunction<T, ?>, V> params, boolean null2IsNull) {
        this.delegate.allEq(condition, filter, params, null2IsNull);
        return this;
    }

    @Override
    public LambdaQuery2<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.eq(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> ne(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.ne(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> gt(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.gt(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> ge(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.ge(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> lt(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.lt(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> le(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.le(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        this.delegate.between(condition, column, val1, val2);
        return this;
    }

    @Override
    public LambdaQuery2<T> notBetween(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        this.delegate.notBetween(condition, column, val1, val2);
        return this;
    }

    @Override
    public LambdaQuery2<T> like(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.like(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> notLike(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.notLike(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> notLikeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.notLikeLeft(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> notLikeRight(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.notLikeRight(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> likeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.likeLeft(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> likeRight(boolean condition, SFunction<T, ?> column, Object val) {
        this.delegate.likeRight(condition, column, val);
        return this;
    }

    @Override
    public LambdaQuery2<T> isNull(boolean condition, SFunction<T, ?> column) {
        this.delegate.isNull(condition, column);
        return this;
    }

    @Override
    public LambdaQuery2<T> isNotNull(boolean condition, SFunction<T, ?> column) {
        this.delegate.isNotNull(condition, column);
        return this;
    }

    @Override
    public LambdaQuery2<T> in(boolean condition, SFunction<T, ?> column, Collection<?> coll) {
        this.delegate.in(condition, column, coll);
        return this;
    }

    @Override
    public LambdaQuery2<T> in(boolean condition, SFunction<T, ?> column, Object... values) {
        this.delegate.in(condition, column, values);
        return this;
    }

    @Override
    public LambdaQuery2<T> notIn(boolean condition, SFunction<T, ?> column, Collection<?> coll) {
        this.delegate.notIn(condition, column, coll);
        return this;
    }

    @Override
    public LambdaQuery2<T> notIn(boolean condition, SFunction<T, ?> column, Object... values) {
        this.delegate.notIn(condition, column, values);
        return this;
    }

    @Override
    public LambdaQuery2<T> inSql(boolean condition, SFunction<T, ?> column, String inValue) {
        this.delegate.inSql(condition, column, inValue);
        return this;
    }

    @Override
    public LambdaQuery2<T> gtSql(boolean condition, SFunction<T, ?> column, String inValue) {
        this.delegate.gtSql(condition, column, inValue);
        return this;
    }

    @Override
    public LambdaQuery2<T> geSql(boolean condition, SFunction<T, ?> column, String inValue) {
        this.delegate.geSql(condition, column, inValue);
        return this;
    }

    @Override
    public LambdaQuery2<T> ltSql(boolean condition, SFunction<T, ?> column, String inValue) {
        this.delegate.ltSql(condition, column, inValue);
        return this;
    }

    @Override
    public LambdaQuery2<T> leSql(boolean condition, SFunction<T, ?> column, String inValue) {
        this.delegate.le(condition, column, inValue);
        return this;
    }

    @Override
    public LambdaQuery2<T> notInSql(boolean condition, SFunction<T, ?> column, String inValue) {
        this.delegate.notInSql(condition, column, inValue);
        return this;
    }

    @Override
    public LambdaQuery2<T> groupBy(boolean condition, SFunction<T, ?> column) {
        this.delegate.groupBy(condition, column);
        return this;
    }

    @Override
    public LambdaQuery2<T> groupBy(boolean condition, List<SFunction<T, ?>> columns) {
        this.delegate.groupBy(condition, columns);
        return this;
    }

    @SafeVarargs
    @Override
    public final LambdaQuery2<T> groupBy(boolean condition, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        this.delegate.groupBy(condition, column, columns);
        return this;
    }

    @Override
    public LambdaQuery2<T> orderBy(boolean condition, boolean isAsc, SFunction<T, ?> column) {
        this.delegate.orderBy(condition, isAsc, column);
        return this;
    }

    @Override
    public LambdaQuery2<T> orderBy(boolean condition, boolean isAsc, List<SFunction<T, ?>> columns) {
        this.delegate.orderBy(condition, isAsc, columns);
        return this;
    }

    @SafeVarargs
    @Override
    public final LambdaQuery2<T> orderBy(boolean condition, boolean isAsc, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        this.delegate.orderBy(condition, isAsc, column, columns);
        return this;
    }

    @Override
    public LambdaQuery2<T> having(boolean condition, String sqlHaving, Object... params) {
        this.delegate.having(condition, sqlHaving, params);
        return this;
    }

    @Override
    public LambdaQuery2<T> func(boolean condition, Consumer<LambdaQuery2<T>> consumer) {
        this.delegate.func(condition, it -> consumer.accept(this));
        return this;
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return null;
    }

    @Override
    public Wrapper<T> getWrapper() {
        return this.delegate;
    }

    @Override
    public Class<T> getEntityClass() {
        return null;
    }

    @Override
    public LambdaQuery2<T> or(boolean condition) {
        this.delegate.or(condition);
        return this;
    }

    @Override
    public LambdaQuery2<T> apply(boolean condition, String applySql, Object... values) {
        this.delegate.apply(condition, applySql, values);
        return this;
    }

    @Override
    public LambdaQuery2<T> last(boolean condition, String lastSql) {
        this.delegate.last(condition, lastSql);
        return this;
    }

    @Override
    public LambdaQuery2<T> comment(boolean condition, String comment) {
        this.delegate.comment(condition, comment);
        return this;
    }

    @Override
    public LambdaQuery2<T> first(boolean condition, String firstSql) {
        this.delegate.first(condition, firstSql);
        return this;
    }

    @Override
    public LambdaQuery2<T> exists(boolean condition, String existsSql, Object... values) {
        this.delegate.exists(condition, existsSql, values);
        return this;
    }

    @Override
    public LambdaQuery2<T> notExists(boolean condition, String existsSql, Object... values) {
        this.delegate.notExists(condition, existsSql, values);
        return this;
    }


    @Override
    public LambdaQuery2<T> and(boolean condition, Consumer<LambdaQueryWrapper<T>> consumer) {
        this.delegate.and(condition, consumer);
        return this;
    }

    @Override
    public LambdaQuery2<T> or(boolean condition, Consumer<LambdaQueryWrapper<T>> consumer) {
        this.delegate.or(condition, consumer);
        return this;
    }

    @Override
    public LambdaQuery2<T> nested(boolean condition, Consumer<LambdaQueryWrapper<T>> consumer) {
        this.delegate.nested(condition, consumer);
        return this;
    }

    @Override
    public LambdaQuery2<T> not(boolean condition, Consumer<LambdaQueryWrapper<T>> consumer) {
        this.delegate.not(condition, consumer);
        return this;
    }
}
