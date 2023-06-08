package tydic.framework.starter.mybatis.repo.expand.repo.method;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.Repo;
import tydic.framework.starter.mybatis.repo.lambda.query.LambdaQuery;
import tydic.framework.starter.mybatis.repo.lambda.update.LambdaUpdate;


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
