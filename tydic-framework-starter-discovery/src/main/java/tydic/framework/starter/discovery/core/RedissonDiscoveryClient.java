package tydic.framework.starter.discovery.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import tydic.framework.starter.cache.redis.RedissonClients;
import tydic.framework.starter.discovery.TydicDiscoveryProperties;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RedissonDiscoveryClient implements DiscoveryClient {
    private final TydicDiscoveryProperties discoveryProperties;
    private final RedissonClients redissonClient;

    @Override
    public String description() {
        return "Redisson Discovery Client";
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        log.info("getInstances {}", serviceId);
        return null;
    }

    @Override
    public List<String> getServices() {
        log.info("getServices {}");
        return null;
    }

}
