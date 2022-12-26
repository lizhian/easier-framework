package tydic.framework.starter.innerRequest;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tydic.framework.starter.innerRequest.core.InnerRequestClient;
import tydic.framework.starter.innerRequest.core.InnerRequestServer;
import tydic.framework.starter.innerRequest.template.DefaultInnerRequestClientTemplate;
import tydic.framework.starter.innerRequest.template.DefaultInnerRequestServerTemplate;
import tydic.framework.starter.innerRequest.template.InnerRequestClientTemplate;
import tydic.framework.starter.innerRequest.template.InnerRequestServerTemplate;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(InnerRequestProperties.class)
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
    public InnerRequestClient innerRequestClient(InnerRequestClientTemplate template, InnerRequestProperties properties) {
        return new InnerRequestClient(template, properties);
    }


    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public InnerRequestServer innerRequestEndpoint(InnerRequestServerTemplate template) {
        return new InnerRequestServer(template);
    }
}
