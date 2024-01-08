package easier.framework.starter.mybatis.lambda;

import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

public interface WrapperInterface<R, W, T> extends Compare<R, SFunction<T, ?>>, Nested<W, R>, Join<R>, Func<R, SFunction<T, ?>> {
}
