package easier.framework.starter.discovery.core;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.plugin.cache.RedisSources;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.cache.redis.RedissonClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class RedissonDiscoveryClient implements DiscoveryClient {
    private final RedissonClients redissonClients;

    @Override
    public String description() {
        return "RedissonDiscoveryClient";
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        return this.redissonClients
                .getClient(RedisSources.discovery)
                .<String, ServiceInstance>getMapCache("Easier:Discovery:" + serviceId)
                .values()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getServices() {
        Iterable<String> keys = this.redissonClients
                .getClient(RedisSources.discovery)
                .getKeys()
                .getKeysByPattern("Easier:Discovery:*");
        return CollUtil.newArrayList(keys)
                .stream()
                .map(key -> StrUtil.removePrefix(key, "Easier:Discovery:"))
                .collect(Collectors.toList());
    }

}
