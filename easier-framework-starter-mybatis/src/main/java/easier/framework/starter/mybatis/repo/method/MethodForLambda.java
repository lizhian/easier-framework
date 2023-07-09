package easier.framework.starter.mybatis.repo.method;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.toolkit.MPJWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import easier.framework.starter.mybatis.lambda.query.LambdaQuery;
import easier.framework.starter.mybatis.lambda.update.LambdaUpdate;
import easier.framework.starter.mybatis.repo.IRepo;


/*
 * 查询单条数据
 */
public interface MethodForLambda<T> extends IRepo<T> {


    /**
     * 新建查询
     *
     * @return {@code LambdaQuery<T>}
     */
    default LambdaQuery<T> newQuery() {
        BaseMapper<T> baseMapper = this.repo().getBaseMapper();
        Class<T> entityClass = this.repo().getEntityClass();
        return new LambdaQuery<>(baseMapper, entityClass);
    }

    /**
     * 新建关联查询
     */
    default MPJLambdaWrapper<T> newJoinQuery() {
        return MPJWrappers.lambdaJoin(this.repo().getEntityClass());
    }

    /**
     * 新建更新
     */
    default LambdaUpdate<T> newUpdate() {
        BaseMapper<T> baseMapper = this.repo().getBaseMapper();
        Class<T> entityClass = this.repo().getEntityClass();
        return new LambdaUpdate<>(baseMapper, entityClass);
    }
}
