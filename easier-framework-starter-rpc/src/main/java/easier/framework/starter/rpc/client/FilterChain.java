package easier.framework.starter.rpc.client;

import easier.framework.starter.rpc.model.RpcRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "of")
public class FilterChain {
    private final List<EasierRpcClientFilter> filters;
    private final Consumer<RpcRequest> next;
    //@Builder.Default
    private int location = 0;

    public void doFilter(RpcRequest request) {
        try {
            int index = this.location++;
            if (index >= this.filters.size()) {
                this.next.accept(request);
            } else {
                this.filters.get(index).doFilter(request, this);
            }
        } finally {
            this.location--;
        }

    }
}
