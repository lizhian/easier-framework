package easier.framework.starter.mybatis.repo.expand.lambda.holder;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import easier.framework.core.domain.Between;
import easier.framework.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class ConditionHolder<T, W extends AbstractChainWrapper<T, SFunction<T, ?>, W, ?>> {
    public static final Predicate<Object> NOT_NULL_CONDITION = Objects::nonNull;
    public static final Predicate<Object> NOT_BLANK_CONDITION = it -> it != null && StrUtil.isNotBlank(it.toString());
    private final W wrapper;
    private final Predicate<Object> condition;

    public ConditionHolder<T, W> toNotNull() {
        return new ConditionHolder<>(this.wrapper, NOT_NULL_CONDITION);
    }

    public ConditionHolder<T, W> toNotBlank() {
        return new ConditionHolder<>(this.wrapper, NOT_BLANK_CONDITION);
    }

    public NotEmptyHolder<T, W> toNotEmpty() {
        return new NotEmptyHolder<>(this.wrapper);
    }

    public W end() {
        return this.wrapper;
    }

    public ConditionHolder<T, W> eq(SFunction<T, ?> column, Object value) {
        this.wrapper.eq(this.condition.test(value), column, value);
        return this;
    }

    public ConditionHolder<T, W> ne(SFunction<T, ?> column, Object value) {
        this.wrapper.ne(this.condition.test(value), column, value);
        return this;
    }

    public ConditionHolder<T, W> gt(SFunction<T, ?> column, Object value) {
        this.wrapper.gt(this.condition.test(value), column, value);
        return this;
    }

    public ConditionHolder<T, W> ge(SFunction<T, ?> column, Object value) {
        this.wrapper.ge(this.condition.test(value), column, value);
        return this;
    }

    public ConditionHolder<T, W> lt(SFunction<T, ?> column, Object value) {
        this.wrapper.lt(this.condition.test(value), column, value);
        return this;
    }

    public ConditionHolder<T, W> le(SFunction<T, ?> column, Object value) {
        this.wrapper.le(this.condition.test(value), column, value);
        return this;
    }

    public ConditionHolder<T, W> between(SFunction<T, ?> column, Object min, Object max) {
        boolean conditionBatch = this.condition.test(min) && this.condition.test(max);
        this.wrapper.between(conditionBatch, column, min, max);
        return this;
    }

    public <V> ConditionHolder<T, W> between(SFunction<T, ?> column, Between<V> between) {
        if (between == null) {
            return this;
        }
        V min = between.getMin();
        if (between.hasMin() && this.condition.test(min)) {
            if (between.isIncludeMin()) {
                this.wrapper.ge(column, min);
            } else {
                this.wrapper.gt(column, min);
            }
        }
        V max = between.getMax();
        if (between.hasMax() && this.condition.test(max)) {
            if (between.isIncludeMax()) {
                this.wrapper.le(column, max);
            } else {
                this.wrapper.lt(column, max);
            }
        }
        return this;
    }

    public <V> ConditionHolder<T, W> notBetween(SFunction<T, ?> column, V min, V max) {
        boolean conditionBatch = this.condition.test(min) && this.condition.test(max);
        this.wrapper.notBetween(conditionBatch, column, min, max);
        return this;
    }

    public <V> ConditionHolder<T, W> notBetween(SFunction<T, ?> column, Between<V> between) {
        if (between == null) {
            return this;
        }
        V min = between.getMin();
        if (between.hasMin() && this.condition.test(min)) {
            if (between.isIncludeMin()) {
                this.wrapper.lt(column, min);
            } else {
                this.wrapper.le(column, min);
            }
        }
        V max = between.getMax();
        if (between.hasMax() && this.condition.test(max)) {
            if (between.isIncludeMax()) {
                this.wrapper.gt(column, max);
            } else {
                this.wrapper.ge(column, max);
            }
        }
        return this;
    }

    public ConditionHolder<T, W> like(SFunction<T, ?> column, Object value) {
        this.wrapper.like(this.condition.test(value), column, value);
        return this;
    }

    public ConditionHolder<T, W> notLike(SFunction<T, ?> column, Object value) {
        this.wrapper.notLike(this.condition.test(value), column, value);
        return this;
    }

    public ConditionHolder<T, W> likeLeft(SFunction<T, ?> column, Object value) {
        this.wrapper.likeLeft(this.condition.test(value), column, value);
        return this;
    }

    public ConditionHolder<T, W> likeRight(SFunction<T, ?> column, Object value) {
        this.wrapper.likeRight(this.condition.test(value), column, value);
        return this;
    }

    public ConditionHolder<T, W> inSql(SFunction<T, ?> column, String inValue) {
        this.wrapper.inSql(this.condition.test(inValue), column, inValue);
        return this;
    }

    public ConditionHolder<T, W> notInSql(SFunction<T, ?> column, String inValue) {
        this.wrapper.notInSql(this.condition.test(inValue), column, inValue);
        return this;
    }

}
