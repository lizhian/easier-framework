package easier.framework.starter.rpc.client.filter;

import cn.hutool.core.util.ArrayUtil;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.util.ExtensionCore;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.rpc.client.EasierRpcClientFilter;
import easier.framework.starter.rpc.client.FilterChain;
import easier.framework.starter.rpc.model.RpcRequest;
import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(ExtensionCore.class)
public class FinalValidFilter implements EasierRpcClientFilter {

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    @SneakyThrows
    public void doFilter(RpcRequest request, FilterChain filterChain) {
        String host = request.getHost();
        if (StrUtil.isBlank(host)) {
            throw FrameworkException.of("远程服务调用,客户端异常,无效的[host]:{}", host);
        }
        if (host.endsWith("/")) {
            host = StrUtil.removeSuffix(host, "/");
        }
        if (!host.startsWith("http")) {
            host = request.isHttps() ? "https://" : "http://" + host;
        }
        String path = request.getPath().orEmpty();
        if (path.endsWith("/")) {
            path = StrUtil.removeSuffix(path, "/");
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        byte[] requestBodyBytes = request.getRequestBodyBytes();
        if (ArrayUtil.isEmpty(requestBodyBytes)) {
            throw FrameworkException.of("远程服务调用,客户端异常,请求体为空");
        }
        request.setHost(host);
        request.setPath(path);
        request.debug("请求链接: {}", host + path);
        request.debug("请求头: {}", request.getHeaders());
        filterChain.doFilter(request);
    }


}
