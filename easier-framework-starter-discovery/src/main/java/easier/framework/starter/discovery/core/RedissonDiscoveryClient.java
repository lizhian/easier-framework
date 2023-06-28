package easier.framework.starter.discovery.core;

import easier.framework.starter.cache.redis.RedissonClients;
import easier.framework.starter.discovery.EasierDiscoveryProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RedissonDiscoveryClient implements DiscoveryClient {
    private final EasierDiscoveryProperties discoveryProperties;
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
