package easier.framework.test.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.PageParam;
import easier.framework.core.util.ExtensionCore;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.eo.RunningLog;
import easier.framework.test.qo.RunningLogQo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;


/**
 * 运行日志服务
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Service
@RequiredArgsConstructor
@ExtensionMethod(ExtensionCore.class)
public class RunningLogService {

    private final Repo<RunningLog> _running_log = Repos.of(RunningLog.class);


    public Page<RunningLog> page(PageParam pageParam, RunningLogQo qo) {
        String traceId = qo.getTraceId();
        if (StrUtil.isNotBlank(traceId)) {
            return this._running_log.newQuery()
                    .eq(RunningLog::getTraceId, traceId)
                    .whenNotBlank()
                    .eq(RunningLog::getServerName, qo.getServerName())
                    .eq(RunningLog::getAppName, qo.getAppName())
                    .eq(RunningLog::getEnv, qo.getEnv())
                    .eq(RunningLog::getLogLevel, qo.getLogLevel())
                    .end()
                    .orderByAsc(RunningLog::getDtTime)
                    .orderByAsc(RunningLog::getSeq)
                    .page(pageParam.toPage());
        }
        return this._running_log.newQuery()
                .whenNotBlank()
                .eq(RunningLog::getServerName, qo.getServerName())
                .eq(RunningLog::getAppName, qo.getAppName())
                .eq(RunningLog::getEnv, qo.getEnv())
                .eq(RunningLog::getLogLevel, qo.getLogLevel())
                .end()
                .orderByDesc(RunningLog::getDtTime)
                .orderByDesc(RunningLog::getSeq)
                .page(pageParam.toPage());
    }
}
