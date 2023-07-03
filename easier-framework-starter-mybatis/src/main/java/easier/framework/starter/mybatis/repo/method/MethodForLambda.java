package easier.framework.starter.mybatis.repo.method;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import easier.framework.core.proxy.TypedSelf;
import easier.framework.starter.mybatis.lambda.query.LambdaQuery;
import easier.framework.starter.mybatis.lambda.update.LambdaUpdate;
import easier.framework.starter.mybatis.repo.Repo;


/*
 * 查询单条数据
 */
public interface MethodForLambda<T> extends TypedSelf<Repo<T>> {


    /**
     * 新建查询
     */
    default LambdaQuery<T> newQuery() {
        BaseMapper<T> baseMapper = this.self().getBaseMapper();
        Class<T> entityClass = this.self().getEntityClass();
        return new LambdaQuery<>(baseMapper, entityClass);
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
        BaseMapper<T> baseMapper = this.self().getBaseMapper();
        Class<T> entityClass = this.self().getEntityClass();
        return new LambdaUpdate<>(baseMapper, entityClass);
    }
}
