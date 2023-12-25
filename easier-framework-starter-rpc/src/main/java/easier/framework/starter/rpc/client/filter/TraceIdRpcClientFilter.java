package easier.framework.starter.rpc.client.filter;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.Easier;
import easier.framework.starter.rpc.client.EasierRpcClientFilter;
import easier.framework.starter.rpc.client.FilterChain;
import easier.framework.starter.rpc.model.RpcRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;

import java.util.List;
import java.util.Map;

@Slf4j
@ConditionalOnDiscoveryEnabled
public class TraceIdRpcClientFilter implements EasierRpcClientFilter {

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 100;
    }

    @Override
    @SneakyThrows
    public void doFilter(RpcRequest request, FilterChain filterChain) {
        // 设置追踪码
        String traceId = Easier.TraceId.getOrReset();
        Map<String, List<String>> headers = request.getHeaders();
        if (!headers.containsKey(Easier.TraceId.x_trace_id)) {
            headers.put(Easier.TraceId.x_trace_id, CollUtil.newArrayList(traceId));
            request.debug("添加 {}: {}", Easier.TraceId.x_trace_id, traceId);
        }
        filterChain.doFilter(request);
    }
}
