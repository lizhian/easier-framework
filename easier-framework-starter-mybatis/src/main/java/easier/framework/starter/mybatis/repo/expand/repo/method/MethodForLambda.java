package easier.framework.starter.mybatis.repo.expand.repo.method;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import easier.framework.core.proxy.TypedSelf;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.lambda.query.LambdaQuery;
import easier.framework.starter.mybatis.repo.lambda.update.LambdaUpdate;


/*
 * 查询单条数据
 */
public interface MethodForLambda<T> extends TypedSelf<Repo<T>> {


    /**
     * 新建查询
     */
    default LambdaQuery<T> newQuery() {
        return new LambdaQuery<>(this.self().getBaseMapper());
    }

    /**
     * 新建关联查询
     */
    default MPJLambdaWrapper<T> newJoinQuery() {
        return new MPJLambdaWrapper<>(this.self().getEntityClass());
    }

    /**
     * 新建更新
     */
    default LambdaUpdate<T> newUpdate() {
        return new LambdaUpdate<>(this.self().getBaseMapper());
    }
}
