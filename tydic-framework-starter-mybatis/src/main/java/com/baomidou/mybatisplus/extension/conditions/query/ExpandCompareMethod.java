package com.baomidou.mybatisplus.extension.conditions.query;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.core.domain.Between;
import tydic.framework.core.proxy.TypedSelf;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * 拓展条件方法
 */
public interface ExpandCompareMethod<T> extends TypedSelf<LambdaQueryChainWrapper<T>> {

    /**
     * 字段非空字符串
     */
    default WrapperHolderForValue<T, String> ifNotBlank() {
        return new WrapperHolderForValue<>(StrUtil::isNotBlank, this.self());
    }

    /**
     * 字段非空
     */
    default WrapperHolderForValue<T, Object> ifNotNull() {
        return new WrapperHolderForValue<>(ObjectUtil::isNotNull, this.self());
    }

    /**
     * 集合非空
     */
    default WrapperHolderForCollection<T> ifNotEmpty() {
        return new WrapperHolderForCollection<>(CollUtil::isNotEmpty, this.self());
    }

    record WrapperHolderForValue<T, C>(Predicate<C> condition, LambdaQueryChainWrapper<T> wrapper) {

        public <V extends C> LambdaQueryChainWrapper<T> eq(SFunction<T, V> column, V value) {
            return this.wrapper.eq(this.condition.test(value), column, value);
        }

        public <V extends C> LambdaQueryChainWrapper<T> ne(SFunction<T, V> column, V value) {
            return this.wrapper.ne(this.condition.test(value), column, value);
        }

        public <V extends C> LambdaQueryChainWrapper<T> gt(SFunction<T, V> column, V value) {
            return this.wrapper.gt(this.condition.test(value), column, value);
        }

        public <V extends C> LambdaQueryChainWrapper<T> ge(SFunction<T, V> column, V value) {
            return this.wrapper.ge(this.condition.test(value), column, value);

        }

        public <V extends C> LambdaQueryChainWrapper<T> lt(SFunction<T, V> column, V value) {
            return this.wrapper.lt(this.condition.test(value), column, value);
        }

        public <V extends C> LambdaQueryChainWrapper<T> le(SFunction<T, V> column, V value) {
            return this.wrapper.le(this.condition.test(value), column, value);
        }

        public <V extends C> LambdaQueryChainWrapper<T> like(SFunction<T, V> column, V value) {
            return this.wrapper.like(this.condition.test(value), column, value);
        }

        public <V extends C> LambdaQueryChainWrapper<T> notLike(SFunction<T, V> column, V value) {
            return this.wrapper.notLike(this.condition.test(value), column, value);
        }

        public <V extends C> LambdaQueryChainWrapper<T> likeLeft(SFunction<T, V> column, V value) {
            return this.wrapper.likeLeft(this.condition.test(value), column, value);
        }

        public <V extends C> LambdaQueryChainWrapper<T> likeRight(SFunction<T, V> column, V value) {
            return this.wrapper.likeRight(this.condition.test(value), column, value);
        }

        public <V extends C> LambdaQueryChainWrapper<T> between(SFunction<T, V> column, V min, V max) {
            boolean bothCondition = this.condition.test(min) && this.condition.test(max);
            return this.wrapper.between(bothCondition, column, min, max);
        }

        public <V extends C> LambdaQueryChainWrapper<T> between(SFunction<T, V> column, Between<V> between) {
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

        public <V extends C> LambdaQueryChainWrapper<T> notBetween(SFunction<T, V> column, V min, V max) {
            boolean bothCondition = this.condition.test(min) && this.condition.test(max);
            return this.wrapper.notBetween(bothCondition, column, min, max);
        }

        public <V extends C> LambdaQueryChainWrapper<T> notBetween(SFunction<T, V> column, Between<V> between) {
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

    record WrapperHolderForCollection<T>(Predicate<Collection<?>> condition, LambdaQueryChainWrapper<T> wrapper) {
        public <V> LambdaQueryChainWrapper<T> in(SFunction<T, V> column, Collection<V> values) {
            return this.wrapper.in(this.condition.test(values), column, values);
        }

        @SafeVarargs
        public final <V> LambdaQueryChainWrapper<T> in(SFunction<T, V> column, V... values) {
            return this.in(column, CollUtil.newArrayList(values));
        }

        public <V> LambdaQueryChainWrapper<T> notIn(SFunction<T, V> column, Collection<V> values) {
            return this.wrapper.in(this.condition.test(values), column, values);
        }

        @SafeVarargs
        public final <V> LambdaQueryChainWrapper<T> notIn(SFunction<T, V> column, V... values) {
            return this.notIn(column, CollUtil.newArrayList(values));
        }

    }
}
