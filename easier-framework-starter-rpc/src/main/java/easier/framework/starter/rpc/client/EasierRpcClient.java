package easier.framework.starter.rpc.client;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import easier.framework.core.plugin.rpc.EasierRPC;
import easier.framework.core.plugin.rpc.RpcClient;
import easier.framework.starter.rpc.EasierRpcProperties;
import easier.framework.starter.rpc.model.RpcRequest;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class EasierRpcClient implements EasierRPC.Client {
    private final Map<Method, RpcClient> CACHE = new ConcurrentHashMap<>();
    private final EasierRpcProperties properties;
    private final List<EasierRpcClientFilter> filters;

    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }
        RpcClient rpcClient = this.CACHE.computeIfAbsent(method, it -> it.getDeclaringClass().getAnnotation(RpcClient.class));
        RpcRequest request = RpcRequest.builder()
                .method(method)
                .args(args)
                .host(rpcClient.host())
                .path(rpcClient.path())
                .type(rpcClient.type())
                .readTimeout(this.properties.getReadTimeout())
                .connectionTimeout(this.properties.getConnectionTimeout())
                .build();
        FilterChain.of(this.filters, this::execute).doFilter(request);
        return request.getResult();
    }

    private void execute(RpcRequest request) {
        String url = request.getHost() + request.getPath();
        @Cleanup HttpResponse execute = HttpUtil
                .createPost(url)
                .body(request.getRequestBodyBytes())
                .header(request.getHeaders())
                .contentType(ContentType.JSON.getValue())
                .setConnectionTimeout(request.getConnectionTimeout())
                .setReadTimeout(request.getReadTimeout())
                .disableCache()
                .disableCookie()
                .execute();
        if (execute.isOk()) {
            byte[] responseBodyBytes = execute.bodyBytes();
            request.setResponseBodyBytes(responseBodyBytes);
        }
        request.setResponseStatus(execute.getStatus());

    }
}
