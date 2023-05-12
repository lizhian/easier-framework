package tydic.framework.starter.mybatis.repo.expand.lambda.method;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.expand.lambda.holder.ConditionHolder;
import tydic.framework.starter.mybatis.repo.expand.lambda.holder.NotEmptyHolder;

import java.util.function.Consumer;

public interface WhenMethod<T, SELF extends AbstractChainWrapper<T, SFunction<T, ?>, SELF, ?>> extends TypedSelf<SELF> {
    default ConditionHolder<T, SELF> whenNotNull() {
        return new ConditionHolder<>(this.self(), ConditionHolder.NOT_NULL_CONDITION);
    }

    default SELF whenNotNull(Consumer<ConditionHolder<T, SELF>> consumer) {
        consumer.accept(this.whenNotNull());
        return this.self();
    }

    default ConditionHolder<T, SELF> whenNotBlank() {
        return new ConditionHolder<>(this.self(), ConditionHolder.NOT_BLANK_CONDITION);
    }

    default SELF whenNotBlank(Consumer<ConditionHolder<T, SELF>> consumer) {
        consumer.accept(this.whenNotBlank());
        return this.self();
    }

    default NotEmptyHolder<T, SELF> whenNotEmpty() {
        return new NotEmptyHolder<>(this.self());
    }

    default SELF whenNotEmpty(Consumer<NotEmptyHolder<T, SELF>> consumer) {
        consumer.accept(this.whenNotEmpty());
        return this.self();
    }
}
