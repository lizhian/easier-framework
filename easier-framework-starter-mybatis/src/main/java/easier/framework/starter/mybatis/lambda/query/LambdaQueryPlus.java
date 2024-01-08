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

public class LambdaQueryPlus<T> implements QueryWrapper<LambdaQueryPlus<T>, T> {
    private final LambdaQueryWrapper<T> delegate;

    public LambdaQueryPlus(Class<T> entityClass) {
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

    @Override
    public <V> LambdaQueryPlus<T> allEq(boolean condition, Map<SFunction<T, ?>, V> params, boolean null2IsNull) {
        return null;
    }

    @Override
    public <V> LambdaQueryPlus<T> allEq(boolean condition, BiPredicate<SFunction<T, ?>, V> filter, Map<SFunction<T, ?>, V> params, boolean null2IsNull) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> ne(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> gt(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> ge(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> lt(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> le(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> notBetween(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> like(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> notLike(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> notLikeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> notLikeRight(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> likeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> likeRight(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> isNull(boolean condition, SFunction<T, ?> column) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> isNotNull(boolean condition, SFunction<T, ?> column) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> in(boolean condition, SFunction<T, ?> column, Collection<?> coll) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> in(boolean condition, SFunction<T, ?> column, Object... values) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> notIn(boolean condition, SFunction<T, ?> column, Collection<?> coll) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> notIn(boolean condition, SFunction<T, ?> column, Object... values) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> inSql(boolean condition, SFunction<T, ?> column, String inValue) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> gtSql(boolean condition, SFunction<T, ?> column, String inValue) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> geSql(boolean condition, SFunction<T, ?> column, String inValue) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> ltSql(boolean condition, SFunction<T, ?> column, String inValue) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> leSql(boolean condition, SFunction<T, ?> column, String inValue) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> notInSql(boolean condition, SFunction<T, ?> column, String inValue) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> groupBy(boolean condition, SFunction<T, ?> column) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> groupBy(boolean condition, List<SFunction<T, ?>> columns) {
        return null;
    }

    @SafeVarargs
    @Override
    public final LambdaQueryPlus<T> groupBy(boolean condition, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> orderBy(boolean condition, boolean isAsc, SFunction<T, ?> column) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> orderBy(boolean condition, boolean isAsc, List<SFunction<T, ?>> columns) {
        return null;
    }

    @SafeVarargs
    @Override
    public final LambdaQueryPlus<T> orderBy(boolean condition, boolean isAsc, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> having(boolean condition, String sqlHaving, Object... params) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> func(boolean condition, Consumer<LambdaQueryPlus<T>> consumer) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> or(boolean condition) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> apply(boolean condition, String applySql, Object... values) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> last(boolean condition, String lastSql) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> comment(boolean condition, String comment) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> first(boolean condition, String firstSql) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> exists(boolean condition, String existsSql, Object... values) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> notExists(boolean condition, String existsSql, Object... values) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> and(boolean condition, Consumer<LambdaQueryWrapper<T>> consumer) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> or(boolean condition, Consumer<LambdaQueryWrapper<T>> consumer) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> nested(boolean condition, Consumer<LambdaQueryWrapper<T>> consumer) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> not(boolean condition, Consumer<LambdaQueryWrapper<T>> consumer) {
        return null;
    }

    @SafeVarargs
    @Override
    public final LambdaQueryPlus<T> select(SFunction<T, ?>... columns) {
        return null;
    }

    @Override
    public LambdaQueryPlus<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        return null;
    }

    @Override
    public String getSqlSelect() {
        return null;
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return null;
    }

    @Override
    public Wrapper<T> getWrapper() {
        return null;
    }

    @Override
    public Class<T> getEntityClass() {
        return null;
    }
}
