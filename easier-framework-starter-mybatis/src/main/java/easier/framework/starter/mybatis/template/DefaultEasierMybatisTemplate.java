package easier.framework.starter.mybatis.template;

import easier.framework.core.plugin.auth.AuthContext;
import easier.framework.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultEasierMybatisTemplate implements EasierMybatisTemplate {

    @Override
    public String currentHandler() {
        return AuthContext.getAccountOr("unknown");
    }

    @Override
    public String dynamicTableName(String sql, String tableName) {
        return tableName;
    }

    @Override
    public Number nextId(Object entity) {
        return IdUtil.nextId();
    }
}
