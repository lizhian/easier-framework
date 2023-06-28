package easier.framework.starter.discovery;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import cn.hutool.system.oshi.OshiUtil;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.cache.EnableEasierCache;
import easier.framework.starter.discovery.core.RedissonAutoServiceRegistration;
import easier.framework.starter.discovery.core.RedissonDiscoveryClient;
import easier.framework.starter.discovery.core.RedissonRegistration;
import easier.framework.starter.discovery.core.RedissonServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(easier.framework.starter.discovery.EasierDiscoveryProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableEasierCache
@EnableSpringUtil
@AutoConfigureBefore(CompositeDiscoveryClientAutoConfiguration.class)
@Import({RedissonDiscoveryClient.class, RedissonServiceRegistry.class, RedissonAutoServiceRegistration.class})
public class EasierDiscoveryAutoConfiguration {


    @Bean
    public RedissonRegistration redissonRegistration(easier.framework.starter.discovery.EasierDiscoveryProperties properties) {
        RedissonRegistration registration = new RedissonRegistration();
        registration.setServiceId(SpringUtil.getApplicationName());
        registration.setHost(getHost(properties));
        registration.setPort(SpringUtil.getServerPort());
        registration.setSecure(properties.isSecure());
        registration.setGroup(properties.getGroup());
        registration.setWeight(getWeight(properties));
        registration.setMetadata(buildMetadata(properties, registration));
        return registration;
    }


    private String getHost(easier.framework.starter.discovery.EasierDiscoveryProperties properties) {
        String networkCard = properties.getNetworkCard();
        if (StrUtil.isBlank(networkCard)) {
            return NetUtil.getLocalhostStr();
        }
        LinkedHashSet<InetAddress> inetAddresses = NetUtil.localAddressList(
                networkInterface -> networkInterface.getName().equals(networkCard)
                , inetAddress -> !inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address
        );
        if (CollUtil.isNotEmpty(inetAddresses)) {
            InetAddress address = null;
            for (InetAddress inetAddress : inetAddresses) {
                if (!inetAddress.isSiteLocalAddress()) {
                    // 非地区本地地址，指10.0.0.0 ~ 10.255.255.255、172.16.0.0 ~ 172.31.255.255、192.168.0.0 ~ 192.168.255.255
                    return inetAddress.getHostAddress();
                } else if (null == address) {
                    address = inetAddress;
                }
            }
            if (null != address) {
                return address.getHostAddress();
            }
            log.warn("通过网卡未能找到IP");
        }
        return NetUtil.getLocalhostStr();
    }

    private int getWeight(easier.framework.starter.discovery.EasierDiscoveryProperties properties) {
        int weight = properties.getWeight();
        if (weight >= 0) {
            return weight;
        }
        return OshiUtil.getCpuInfo().getCpuNum();
    }

    private Map<String, String> buildMetadata(easier.framework.starter.discovery.EasierDiscoveryProperties properties, RedissonRegistration registration) {
        Map<String, String> metadata = properties.getMetadata();
        metadata.put(RedissonRegistration.Fields.group, registration.getGroup());
        metadata.put(RedissonRegistration.Fields.weight, String.valueOf(registration.getWeight()));
        return metadata;
    }
}
