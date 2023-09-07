package easier.framework.starter.mybatis.interceptor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * sql执行时间统计
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class MybatisTimerInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        TimeInterval timer = DateUtil.timer();
        Object proceed = invocation.proceed();
        this.logTimer(invocation, timer.intervalPretty());
        return proceed;
    }

    private void logTimer(Invocation invocation, String intervalPretty) {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Log statementLog = mappedStatement.getStatementLog();
        if (statementLog.isDebugEnabled()) {
            statementLog.debug("<==      Timer: " + intervalPretty);
        }
    }


}
