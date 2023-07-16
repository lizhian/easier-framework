package easier.framework.starter.rpc.client.filter;

import easier.framework.core.plugin.rpc.enums.HostType;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.rpc.client.EasierRpcClientFilter;
import easier.framework.starter.rpc.client.FilterChain;
import easier.framework.starter.rpc.model.RpcRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;

@Slf4j
@ConditionalOnDiscoveryEnabled
public class PropertyConverterFilter implements EasierRpcClientFilter {

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    @SneakyThrows
    public void doFilter(RpcRequest request, FilterChain filterChain) {
        if (HostType.property.equals(request.getType())) {
            String host = request.getHost();
            String newHost = SpringUtil.getProperty(host);
            if (StrUtil.isBlank(newHost)) {
                request.trace("通过配置文件转换请求地址,未找到对应配置:{}", host);
                filterChain.doFilter(request);
                return;
            }
            request.setHost(newHost);
            request.trace("通过配置文件转换请求地址: {} -> {}", host, newHost);
        }
        filterChain.doFilter(request);
    }


}
