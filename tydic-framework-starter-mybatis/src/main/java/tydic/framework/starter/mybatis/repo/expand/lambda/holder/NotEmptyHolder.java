package tydic.framework.starter.mybatis.repo.expand.lambda.holder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public class NotEmptyHolder<T, W extends AbstractChainWrapper<T, SFunction<T, ?>, W, ?>> {
    private final W wrapper;

    public ConditionHolder<T, W> toNotNull() {
        return new ConditionHolder<>(this.wrapper, ConditionHolder.NOT_NULL_CONDITION);
    }

    public ConditionHolder<T, W> toNotBlank() {
        return new ConditionHolder<>(this.wrapper, ConditionHolder.NOT_BLANK_CONDITION);
    }

    public W end() {
        return this.wrapper;
    }

    public NotEmptyHolder<T, W> in(SFunction<T, ?> column, Object... values) {
        this.wrapper.in(ArrayUtil.isNotEmpty(values), column, values);
        return this;
    }

    public NotEmptyHolder<T, W> in(SFunction<T, ?> column, Collection<?> coll) {
        this.wrapper.in(CollUtil.isNotEmpty(coll), column, coll);
        return this;
    }

    public NotEmptyHolder<T, W> notIn(SFunction<T, ?> column, Object... values) {
        this.wrapper.notIn(ArrayUtil.isNotEmpty(values), column, values);
        return this;
    }

    public NotEmptyHolder<T, W> notIn(SFunction<T, ?> column, Collection<?> coll) {
        this.wrapper.notIn(CollUtil.isNotEmpty(coll), column, coll);
        return this;
    }

}
