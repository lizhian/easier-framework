package easier.framework.starter.rpc.client.filter;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.ZipUtil;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.util.JacksonUtil;
import easier.framework.starter.rpc.client.EasierRpcClientFilter;
import easier.framework.starter.rpc.client.FilterChain;
import easier.framework.starter.rpc.model.RpcRequest;
import easier.framework.starter.rpc.model.RpcRequestBody;
import easier.framework.starter.rpc.model.RpcResponseBody;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;

import java.lang.reflect.Method;

@Slf4j
@ConditionalOnDiscoveryEnabled
public class BodyCodecFilter implements EasierRpcClientFilter {

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    @SneakyThrows
    public void doFilter(RpcRequest request, FilterChain filterChain) {
        Method method = request.getMethod();
        String className = method.getDeclaringClass().getName();
        RpcRequestBody body = RpcRequestBody.builder()
                .className(className)
                .methodName(method.getName())
                .args(request.getArgs())
                .build();
        byte[] requestBodyBytes = ZipUtil.gzip(JacksonUtil.serialize(body));
        request.trace("请求对象大小: {}", DataSizeUtil.format(requestBodyBytes.length));
        request.setRequestBodyBytes(requestBodyBytes);
        filterChain.doFilter(request);
        byte[] responseBodyBytes = request.getResponseBodyBytes();
        if (responseBodyBytes == null) {
            throw FrameworkException.of("远程服务调用,服务端异常,响应体为空,状态码: {}", request.getResponseStatus());
        }
        request.trace("响应对象大小: {}", DataSizeUtil.format(responseBodyBytes.length));
        RpcResponseBody rpcResponseBody = JacksonUtil.deserialize(ZipUtil.unGzip(responseBodyBytes));
        if (rpcResponseBody.isSuccess()) {
            request.setResult(rpcResponseBody.getResult());
            return;
        }
        throw FrameworkException.of("远程服务调用,服务端返回异常: {}", rpcResponseBody.getErrorMessage());
    }
}
