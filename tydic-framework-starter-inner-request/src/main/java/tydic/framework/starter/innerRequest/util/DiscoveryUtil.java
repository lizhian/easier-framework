package tydic.framework.starter.innerRequest.util;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import lombok.experimental.UtilityClass;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import tydic.framework.core.util.SpringUtil;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class DiscoveryUtil {
    public ServiceInstance getInstance(String serviceId) {
        DiscoveryClient discoveryClient = SpringUtil.getAndCache(DiscoveryClient.class);
        //返回地址以及对应权重
        List<WeightRandom.WeightObj<ServiceInstance>> list = discoveryClient.getInstances(serviceId)
                .stream()
                .map(instance -> {
                    String weight = instance.getMetadata().getOrDefault("weight", "1");
                    return new WeightRandom.WeightObj<>(instance, Double.parseDouble(weight));
                })
                .collect(Collectors.toList());
        return RandomUtil.weightRandom(list).next();
    }
}
