package easier.framework.test.api;

import easier.framework.core.plugin.rpc.RpcClient;
import easier.framework.core.plugin.rpc.RpcInterface;
import easier.framework.core.plugin.rpc.enums.HostType;
import easier.framework.test.eo.DictItem;

import java.util.List;

@RpcClient(host = "user-center", type = HostType.discovery)
public interface MyRpc extends RpcInterface {

    DictItem getUser(String id);

    List<DictItem> list();
}
