package easier.framework.starter.mybatis.template;

import easier.framework.core.Easier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultEasierMybatisTemplate implements EasierMybatisTemplate {

    @Override
    public String currentHandler() {
        return Easier.Auth.getAccountOr("unknown");
    }

    @Override
    public String dynamicTableName(String sql, String tableName) {
        return tableName;
    }

    @Override
    public Number nextId(Object entity) {
        return Easier.Id.nextId();
    }
}
