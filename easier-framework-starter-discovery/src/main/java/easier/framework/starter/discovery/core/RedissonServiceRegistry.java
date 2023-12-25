package easier.framework.starter.discovery.core;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.system.oshi.OshiUtil;
import easier.framework.core.plugin.cache.RedisSources;
import easier.framework.core.plugin.job.LoopJob;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.cache.redis.RedissonClients;
import easier.framework.starter.discovery.EasierDiscoveryProperties;
import easier.framework.starter.job.loop.LoopJobContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redisson服务注册
 *
 * @author lizhian
 * @date 2023年07月16日
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonServiceRegistry implements DisposableBean, ApplicationListener<WebServerInitializedEvent> {


    private final EasierDiscoveryProperties properties;
    private final RedissonClients redissonClients;

    private boolean registry = false;
    private RedissonServiceInstance serviceInstance;
    private RMapCache<String, RedissonServiceInstance> instanceCache;


    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serviceInstance = this.initServiceInstance(event.getWebServer().getPort());
        this.instanceCache = this.redissonClients
                .getClient(RedisSources.discovery)
                .getMapCache(EasierDiscoveryProperties.REDIS_KEY_PREFIX + this.serviceInstance.getServiceId());
        this.registry = true;
    }


    /**
     * 注册表服务实例
     */
    @LoopJob(delay = 5, timeUnit = TimeUnit.SECONDS, lock = false)
    public void registryServiceInstance() {
        if (!this.registry) {
            return;
        }
        String instanceId = this.serviceInstance.getInstanceId();
        long ttl = LoopJobContext.get().getLoopJob().delay() * 2;
        TimeUnit timeUnit = LoopJobContext.get().getLoopJob().timeUnit();
        this.instanceCache.putAsync(instanceId, this.serviceInstance, ttl, timeUnit);
        this.instanceCache.clearExpireAsync();
    }

    @Override
    public void destroy() throws Exception {
        this.registry = false;
        String instanceId = this.serviceInstance.getInstanceId();
        this.instanceCache.remove(instanceId);
    }

    private RedissonServiceInstance initServiceInstance(int port) {
        RedissonServiceInstance instance = new RedissonServiceInstance();
        instance.setServiceId(SpringUtil.getApplicationName());
        instance.setHost(this.getHost());
        instance.setPort(SpringUtil.getServerPort(port));
        instance.setSecure(this.properties.isSecure());
        instance.setGroup(this.properties.getGroup());
        instance.setWeight(this.getWeight());
        instance.setMetadata(this.buildMetadata(instance));
        String instanceId = StrUtil.format("{}@{}@{}@{}"
                , instance.getServiceId()
                , instance.getGroup()
                , instance.getHost()
                , instance.getPort()
        );
        instance.setInstanceId(instanceId);
        return instance;
    }


    private int getWeight() {
        int weight = this.properties.getWeight();
        if (weight >= 0) {
            return weight;
        }
        return OshiUtil.getCpuInfo().getCpuNum();
    }

    private Map<String, String> buildMetadata(RedissonServiceInstance serviceInstance) {
        Map<String, String> metadata = this.properties.getMetadata();
        metadata.put(RedissonServiceInstance.Fields.group, serviceInstance.getGroup());
        metadata.put(RedissonServiceInstance.Fields.weight, String.valueOf(serviceInstance.getWeight()));
        return metadata;
    }

    private String getHost() {
        List<String> hostPriorities = StrUtil.splitTrim(this.properties.getHostPriorities(), ",");
        if (CollUtil.isEmpty(hostPriorities)) {
            return NetUtil.getLocalhostStr();
        }
        LinkedHashSet<String> localIpv4s = NetUtil.localIpv4s();

        for (String hostPriority : hostPriorities) {
            //根据网段前缀匹配
            String ipMatch = localIpv4s.stream()
                    .filter(ip -> ip.startsWith(hostPriority))
                    .findAny()
                    .orElse(null);
            if (StrUtil.isNotBlank(ipMatch)) {
                return ipMatch;
            }
            //根据网卡名称匹配
            NetworkInterface networkInterface = NetUtil.getNetworkInterface(hostPriority);
            if (networkInterface != null) {
                //额外过滤
                InetAddress inetAddress = this.additionalFilter(networkInterface.getInterfaceAddresses());
                if (inetAddress != null) {
                    return inetAddress.getHostAddress();
                }
            }
        }
        return NetUtil.getLocalhostStr();
    }


    /**
     * 附加过滤器
     *
     * @param addresses 地址
     * @return {@link InetAddress}
     */
    private InetAddress additionalFilter(List<InterfaceAddress> addresses) {
        if (addresses == null) {
            return null;
        }
        InetAddress finalAddress = null;
        for (InterfaceAddress interfaceAddress : addresses) {
            InetAddress inetAddress = interfaceAddress.getAddress();
            if (inetAddress == null) {
                continue;
            }
            // 非loopback地址，指127.*.*.*的地址
            if (inetAddress.isLoopbackAddress()) {
                continue;
            }
            // 需为IPV4地址
            if (!(inetAddress instanceof Inet4Address)) {
                continue;
            }
            if (!inetAddress.isSiteLocalAddress()) {
                // 非地区本地地址，指10.0.0.0 ~ 10.255.255.255、172.16.0.0 ~ 172.31.255.255、192.168.0.0 ~ 192.168.255.255
                return inetAddress;
            }
            if (finalAddress == null) {
                finalAddress = inetAddress;
            }
        }
        return finalAddress;
    }
}
