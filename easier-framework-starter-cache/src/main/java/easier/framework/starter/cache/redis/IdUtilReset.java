package easier.framework.starter.cache.redis;

import easier.framework.core.plugin.cache.RedisSources;
import easier.framework.starter.cache.condition.ConditionalOnRedisSource;
import org.springframework.beans.factory.InitializingBean;


@ConditionalOnRedisSource(RedisSources.snowflake)
public class IdUtilReset implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
