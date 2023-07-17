package easier.framework.starter.rpc;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.starter.rpc.client.EasierRpcClient;
import easier.framework.starter.rpc.client.EasierRpcClientFilter;
import easier.framework.starter.rpc.client.filter.*;
import easier.framework.starter.rpc.server.EasierRpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 更简单rpc自动配置
 *
 * @author lizhian
 * @date 2023年07月16日
 */
@Slf4j
@EnableConfigurationProperties(EasierRpcProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
@Import({
        DebugFilter.class
        , DiscoveryConverterFilter.class
        , PropertyConverterFilter.class
        , BodyCodecFilter.class
        , TraceIdRpcClientFilter.class
        , TokenRpcClientFilter.class
        , FinalValidFilter.class
})
public class EasierRpcAutoConfiguration {

    @Bean
    public EasierRpcClient easierRpcClient(EasierRpcProperties properties, ObjectProvider<EasierRpcClientFilter> filterObjectProvider) {
        List<EasierRpcClientFilter> filters = filterObjectProvider.orderedStream().collect(Collectors.toList());
        return new EasierRpcClient(properties, filters);
    }

    @Bean
    public EasierRpcServer easierRpcServer() {
        return new EasierRpcServer();
    }
}
