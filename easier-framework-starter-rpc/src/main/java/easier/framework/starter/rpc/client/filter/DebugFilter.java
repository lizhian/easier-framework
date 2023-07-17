package easier.framework.starter.rpc.client.filter;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.rpc.client.EasierRpcClientFilter;
import easier.framework.starter.rpc.client.FilterChain;
import easier.framework.starter.rpc.model.RpcRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;

@Slf4j
@ConditionalOnDiscoveryEnabled
public class DebugFilter implements EasierRpcClientFilter {

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    @SneakyThrows
    public void doFilter(RpcRequest request, FilterChain filterChain) {
        TimeInterval timer = DateUtil.timer();
        request.debug("远程服务调用debug\n开始时间: {}", DateTime.now().toMsStr());
        request.debug("接口方法: {}.{}({})"
                , request.getMethod().getDeclaringClass().getName()
                , request.getMethod().getName()
                , request.getMethod().getParameterTypes()
        );
        try {
            filterChain.doFilter(request);
            request.debug("请求完成,时间: {}", DateTime.now().toMsStr());
        } catch (Exception e) {
            request.debug("异常信息: {}", e.getMessage());
            if (e instanceof FrameworkException) {
                throw e;
            }
            throw new FrameworkException(e);
        } finally {
            request.debug("总耗时: {}", timer.intervalPretty());
            if (log.isDebugEnabled()) {
                log.debug(StrUtil.join("\n", request.getDebugMessages()));
            }
        }
    }
}
