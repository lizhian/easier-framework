package easier.framework.starter.rpc.client.filter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.same.SaSameUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import easier.framework.core.Easier;
import easier.framework.starter.rpc.client.EasierRpcClientFilter;
import easier.framework.starter.rpc.client.FilterChain;
import easier.framework.starter.rpc.model.RpcRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class TokenRpcClientFilter implements EasierRpcClientFilter {

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 100;
    }

    @Override
    @SneakyThrows
    public void doFilter(RpcRequest request, FilterChain filterChain) {
        Map<String, List<String>> headers = request.getHeaders();
        if (!headers.containsKey(SaSameUtil.SAME_TOKEN)) {
            // 添加 SAME_TOKEN, 用于校验是否为内部请求
            headers.put(SaSameUtil.SAME_TOKEN, CollUtil.newArrayList(SaSameUtil.getToken()));
            request.debug("添加 {}: {}", SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
        }


        // 如果有token,传递tokenValue,相当于传递认证信息
        String tokenValue = Easier.Auth.tryGetTokenValue();
        if (StrUtil.isNotBlank(tokenValue)) {
            String key = SaManager.getConfig().getTokenName();
            headers.put(key, CollUtil.newArrayList(tokenValue));
            request.debug("添加 {}: {}}", key, tokenValue);
        }
        filterChain.doFilter(request);
    }
}
