package tydic.framework.starter.discovery;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import cn.hutool.system.oshi.OshiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.cache.EnableTydicCache;
import tydic.framework.starter.discovery.core.RedissonAutoServiceRegistration;
import tydic.framework.starter.discovery.core.RedissonDiscoveryClient;
import tydic.framework.starter.discovery.core.RedissonRegistration;
import tydic.framework.starter.discovery.core.RedissonServiceRegistry;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(TydicDiscoveryProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableTydicCache
@EnableSpringUtil
@AutoConfigureBefore(CompositeDiscoveryClientAutoConfiguration.class)
@Import({RedissonDiscoveryClient.class, RedissonServiceRegistry.class, RedissonAutoServiceRegistration.class})
public class TydicDiscoveryAutoConfiguration {


    @Bean
    public RedissonRegistration redissonRegistration(TydicDiscoveryProperties properties) {
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


    private String getHost(TydicDiscoveryProperties properties) {
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

    private int getWeight(TydicDiscoveryProperties properties) {
        int weight = properties.getWeight();
        if (weight >= 0) {
            return weight;
        }
        return OshiUtil.getCpuInfo().getCpuNum();
    }

    private Map<String, String> buildMetadata(TydicDiscoveryProperties properties, RedissonRegistration registration) {
        Map<String, String> metadata = properties.getMetadata();
        metadata.put(RedissonRegistration.Fields.group, registration.getGroup());
        metadata.put(RedissonRegistration.Fields.weight, String.valueOf(registration.getWeight()));
        return metadata;
    }
}
