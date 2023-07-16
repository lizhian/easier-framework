package easier.framework.starter.rpc.client;

import easier.framework.starter.rpc.model.RpcRequest;
import org.springframework.core.Ordered;

public interface EasierRpcClientFilter extends Ordered {
    void doFilter(RpcRequest request, FilterChain filterChain);
}
