package easier.framework.test.rpc;

import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.api.MyRpc;
import easier.framework.test.eo.DictItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyRpcService implements MyRpc {
    @Override
    public DictItem getUser(String id) {
        throw BizException.of("这是一个异常");
    }

    @Override
    public List<DictItem> list() {
        return Repos.of(DictItem.class).listAll();
    }

}
