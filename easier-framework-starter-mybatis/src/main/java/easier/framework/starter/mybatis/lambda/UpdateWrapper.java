package easier.framework.starter.mybatis.lambda;

import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery;

public interface UpdateWrapper<SELF, T> extends
        Query<SELF, T, SFunction<T, ?>>,
        ChainQuery<T>,
        Join<SELF>,
        Func<SELF, SFunction<T, ?>>,
        Compare<SELF, SFunction<T, ?>>,
        Nested<LambdaQueryWrapper<T>, SELF> {
}
