package tydic.framework.starter.auth.dao;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoRedissonJackson;
import lombok.experimental.Delegate;
import org.redisson.api.RedissonClient;
import tydic.framework.core.plugin.cache.RedisSources;
import tydic.framework.starter.cache.redis.RedissonClients;


public class SaTokenDaoForRedissonClients implements SaTokenDao {

    @Delegate(types = SaTokenDao.class)
    private final SaTokenDaoRedissonJackson delegate;

    public SaTokenDaoForRedissonClients(RedissonClients redissonClients) {
        RedissonClient client = redissonClients.get(RedisSources.auth);
        this.delegate = new SaTokenDaoRedissonJackson();
        delegate.init(client);
    }
}
