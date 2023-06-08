package tydic.framework.starter.mybatis.repo.expand.repo.method;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.Repo;

import java.util.Collection;
import java.util.function.BiConsumer;


/*
 * 批量执行方法
 */
public interface MethodForBatch<T> extends TypedSelf<Repo<T>> {

    /**
     * 批量执行
     */
    default <E> boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
        int defaultBatchSize = this.self().getDefaultBatchSize();
        return this.executeBatch(list, defaultBatchSize, consumer);
    }

    /**
     * 批量执行
     */
    default <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        Repo<T> repo = this.self();
        Log log = LogFactory.getLog(repo.getClass());
        Class<T> entityClass = repo.getEntityClass();
        return SqlHelper.executeBatch(entityClass, log, list, batchSize, consumer);
    }
}
