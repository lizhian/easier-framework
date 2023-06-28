package easier.framework.starter.innerRequest;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.starter.innerRequest.core.InnerRequestClient;
import easier.framework.starter.innerRequest.core.InnerRequestServer;
import easier.framework.starter.innerRequest.template.DefaultInnerRequestClientTemplate;
import easier.framework.starter.innerRequest.template.DefaultInnerRequestServerTemplate;
import easier.framework.starter.innerRequest.template.InnerRequestClientTemplate;
import easier.framework.starter.innerRequest.template.InnerRequestServerTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(easier.framework.starter.innerRequest.InnerRequestProperties.class)
@EnableSpringUtil
public class InnerRequestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public InnerRequestServerTemplate innerRequestServerTemplate() {
        return new DefaultInnerRequestServerTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public InnerRequestClientTemplate innerRequestClientTemplate() {
        return new DefaultInnerRequestClientTemplate();
    }


    @Bean
    public InnerRequestClient innerRequestClient(InnerRequestClientTemplate template, easier.framework.starter.innerRequest.InnerRequestProperties properties) {
        return new InnerRequestClient(template, properties);
    }


    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public InnerRequestServer innerRequestEndpoint(InnerRequestServerTemplate template) {
        return new InnerRequestServer(template);
    }
}
