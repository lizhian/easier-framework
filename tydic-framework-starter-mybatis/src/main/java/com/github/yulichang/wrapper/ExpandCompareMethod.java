/*
package com.github.yulichang.wrapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.core.domain.Between;
import tydic.framework.core.proxy.TypedSelf;

import java.util.Collection;
import java.util.function.Predicate;

*/
/**
 * 拓展条件方法
 * <p>
 * 字段非空字符串
 * <p>
 * 字段非空
 * <p>
 * 集合非空
 *//*

public interface ExpandCompareMethod<T> extends TypedSelf<MPJLambdaWrapper<T>> {

    */
/**
 * 字段非空字符串
 *//*

    default WrapperHolderForValue<T, String> ifNotBlank() {
        return new WrapperHolderForValue<>(StrUtil::isNotBlank, this.self());
    }

    */
/**
 * 字段非空
 *//*

    default WrapperHolderForValue<T, Object> ifNotNull() {
        return new WrapperHolderForValue<>(ObjectUtil::isNotNull, this.self());
    }

    */
/**
 * 集合非空
 *//*

    default <V> WrapperHolderForCollection<T> ifNotEmpty() {
        return new WrapperHolderForCollection<>(CollUtil::isNotEmpty, this.self());
    }

    record WrapperHolderForValue<T, C>(Predicate<C> condition, MPJLambdaWrapper<T> wrapper) {

        public <V extends C> MPJLambdaWrapper<T> eq(SFunction<T, V> column, V value) {
            return this.wrapper.eq(this.condition.test(value), column, value);
        }

        public <V extends C> MPJLambdaWrapper<T> ne(SFunction<T, V> column, V value) {
            return this.wrapper.ne(this.condition.test(value), column, value);
        }

        public <V extends C> MPJLambdaWrapper<T> gt(SFunction<T, V> column, V value) {
            return this.wrapper.gt(this.condition.test(value), column, value);
        }

        public <V extends C> MPJLambdaWrapper<T> ge(SFunction<T, V> column, V value) {
            return this.wrapper.ge(this.condition.test(value), column, value);
        }

        public <V extends C> MPJLambdaWrapper<T> lt(SFunction<T, V> column, V value) {
            return this.wrapper.lt(this.condition.test(value), column, value);
        }

        public <V extends C> MPJLambdaWrapper<T> le(SFunction<T, V> column, V value) {
            return this.wrapper.le(this.condition.test(value), column, value);
        }

        public <V extends C> MPJLambdaWrapper<T> like(SFunction<T, V> column, V value) {
            return this.wrapper.like(this.condition.test(value), column, value);
        }

        public <V extends C> MPJLambdaWrapper<T> notLike(SFunction<T, V> column, V value) {
            return this.wrapper.notLike(this.condition.test(value), column, value);
        }

        public <V extends C> MPJLambdaWrapper<T> likeLeft(SFunction<T, V> column, V value) {
            return this.wrapper.likeLeft(this.condition.test(value), column, value);
        }

        public <V extends C> MPJLambdaWrapper<T> likeRight(SFunction<T, V> column, V value) {
            return this.wrapper.likeRight(this.condition.test(value), column, value);
        }

        public <V extends C> MPJLambdaWrapper<T> between(SFunction<T, V> column, V min, V max) {
            boolean bothCondition = this.condition.test(min) && this.condition.test(max);
            return this.wrapper.between(bothCondition, column, min, max);
        }

        public <V extends C> MPJLambdaWrapper<T> between(SFunction<T, V> column, Between<V> between) {
            if (between == null) {
                return this.wrapper;
            }
            if (between.hasMin() && between.isIncludeMin()) {
                this.wrapper.ge(this.condition.test(between.getMin()), column, between.getMin());
            }
            if (between.hasMin() && !between.isIncludeMin()) {
                this.wrapper.gt(this.condition.test(between.getMin()), column, between.getMin());
            }
            if (between.hasMax() && between.isIncludeMax()) {
                this.wrapper.le(this.condition.test(between.getMax()), column, between.getMax());
            }
            if (between.hasMax() && !between.isIncludeMax()) {
                this.wrapper.lt(this.condition.test(between.getMax()), column, between.getMax());
            }
            return this.wrapper;
        }

        public <V extends C> MPJLambdaWrapper<T> notBetween(SFunction<T, V> column, V min, V max) {
            boolean bothCondition = this.condition.test(min) && this.condition.test(max);
            return this.wrapper.notBetween(bothCondition, column, min, max);
        }

        public <V extends C> MPJLambdaWrapper<T> notBetween(SFunction<T, V> column, Between<V> between) {
            if (between == null) {
                return this.wrapper;
            }
            if (between.hasMin() && between.isIncludeMin()) {
                this.wrapper.lt(this.condition.test(between.getMin()), column, between.getMin());
            }
            if (between.hasMin() && !between.isIncludeMin()) {
                this.wrapper.le(this.condition.test(between.getMin()), column, between.getMin());
            }
            if (between.hasMax() && between.isIncludeMax()) {
                this.wrapper.gt(this.condition.test(between.getMax()), column, between.getMax());
            }
            if (between.hasMax() && !between.isIncludeMax()) {
                this.wrapper.ge(this.condition.test(between.getMax()), column, between.getMax());
            }
            return this.wrapper;
        }
    }

    record WrapperHolderForCollection<T>(Predicate<Collection<?>> condition, MPJLambdaWrapper<T> wrapper) {
        public <V> MPJLambdaWrapper<T> in(SFunction<T, V> column, Collection<V> values) {
            return this.wrapper.in(this.condition.test(values), column, values);
        }

        @SafeVarargs
        public final <V> MPJLambdaWrapper<T> in(SFunction<T, V> column, V... values) {
            return this.in(column, CollUtil.newArrayList(values));
        }

        public <V> MPJLambdaWrapper<T> notIn(SFunction<T, V> column, Collection<V> values) {
            return this.wrapper.in(this.condition.test(values), column, values);
        }

        @SafeVarargs
        public final <V> MPJLambdaWrapper<T> notIn(SFunction<T, V> column, V... values) {
            return this.notIn(column, CollUtil.newArrayList(values));
        }
    }
}
*/
