package tydic.framework.starter.mybatis.template;

import lombok.extern.slf4j.Slf4j;
import tydic.framework.core.plugin.auth.AuthContext;
import tydic.framework.core.util.IdUtil;
import tydic.framework.core.util.StrUtil;

@Slf4j
public class DefaultTydicMybatisTemplate implements TydicMybatisTemplate {

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
