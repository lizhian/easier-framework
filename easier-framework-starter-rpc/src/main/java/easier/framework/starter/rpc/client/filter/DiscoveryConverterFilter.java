package easier.framework.starter.rpc.client.filter;

import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.plugin.rpc.enums.HostType;
import easier.framework.starter.rpc.client.EasierRpcClientFilter;
import easier.framework.starter.rpc.client.FilterChain;
import easier.framework.starter.rpc.model.RpcRequest;
import easier.framework.starter.rpc.util.DiscoveryUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.ServiceInstance;

@Slf4j
@ConditionalOnDiscoveryEnabled
public class DiscoveryConverterFilter implements EasierRpcClientFilter {

    @Override
    public int getOrder() {
        return -200;
    }

    @Override
    @SneakyThrows
    public void doFilter(RpcRequest request, FilterChain filterChain) {
        if (HostType.discovery.equals(request.getType())) {
            String host = request.getHost();
            ServiceInstance instance = DiscoveryUtil.getInstance(host);
            if (instance == null) {
                throw FrameworkException.of("远程服务调用,客户端异常,通过服务发现转换请求地址,未找到对应服务:{}", host);
            }
            String newHost = instance.getUri().toString();
            request.setHost(newHost);
            request.trace("通过服务发现转换请求地址: {} -> {}", host, newHost);
        }
        filterChain.doFilter(request);
    }


}
