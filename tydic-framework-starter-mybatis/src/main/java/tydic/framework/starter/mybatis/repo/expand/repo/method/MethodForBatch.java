package tydic.framework.starter.mybatis.repo.expand.repo.method;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.BaseRepo;

import java.util.Collection;
import java.util.function.BiConsumer;


/*
 * 批量执行方法
 */
public interface MethodForBatch<T, SELF extends BaseRepo<T>> extends TypedSelf<SELF> {

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
        SELF self = this.self();
        Log log = LogFactory.getLog(self.getClass());
        Class<T> entityClass = self.getEntityClass();
        return SqlHelper.executeBatch(entityClass, log, list, batchSize, consumer);
    }
}
