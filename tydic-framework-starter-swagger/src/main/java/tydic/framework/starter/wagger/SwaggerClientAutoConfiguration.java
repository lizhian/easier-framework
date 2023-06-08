package tydic.framework.starter.wagger;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.classmate.TypeResolver;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.wagger.plugin.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@EnableKnife4j
@EnableSwagger2WebMvc
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SwaggerClientAutoConfiguration implements WebMvcConfigurer {

    static {
        String key = "spring.mvc.pathmatch.matching-strategy";
        String value = "ant_path_matcher";
        System.setProperty(key, value);
        log.info("写入系统配置【{}={}】", key, value);
    }

    /**
     * 解决兼容问题
     * "org.springframework.web.servlet.mvc.condition.PatternsRequestCondition.getPatterns()" because "this.condition" is null
     */
    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider) {
                    List<RequestMappingInfoHandlerMapping> handlerMappings = (List<RequestMappingInfoHandlerMapping>) ReflectUtil.getFieldValue(bean, "handlerMappings");
                    List<RequestMappingInfoHandlerMapping> copy = handlerMappings.stream()
                            .filter(mapping -> mapping.getPatternParser() == null)
                            .collect(Collectors.toList());
                    handlerMappings.clear();
                    handlerMappings.addAll(copy);
                }
                return bean;
            }
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        //registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setPathMatcher(new AntPathMatcher());
    }

    @Bean
    public PermissionHeaderPlugin permissionHeaderPlugin() {
        return new PermissionHeaderPlugin();
    }

    @Bean
    public EnumExpandedParameterBuilderPlugin enumExpandedParameterBuilderPlugin(TypeResolver typeResolver) {
        return new EnumExpandedParameterBuilderPlugin(typeResolver);
    }

    @Bean
    public EnumModelPropertyPlugin enumModelPropertyPlugin(TypeResolver typeResolver) {
        return new EnumModelPropertyPlugin(typeResolver);
    }

    @Bean
    public EnumParameterBuilderPlugin enumParameterBuilderPlugin(TypeResolver typeResolver) {
        return new EnumParameterBuilderPlugin(typeResolver);
    }

    @Bean
    public MockHeaderSwaggerPlugin mockHeaderSwaggerPlugin() {
        return new MockHeaderSwaggerPlugin();
    }

    @Bean
    public OperationOrderBuilderPlugin operationOrderBuilderPlugin() {
        return new OperationOrderBuilderPlugin();
    }

    @Bean
    public Docket serviceRestApi(@Autowired(required = false) SaTokenConfig saTokenConfig) {
        List<SecurityScheme> securitySchemes = CollUtil.newArrayList(this.securityScheme(saTokenConfig));
        List<SecurityContext> securityContexts = CollUtil.newArrayList(this.securityContext(saTokenConfig));
        return new Docket(DocumentationType.SWAGGER_2)
                .directModelSubstitute(DateTime.class, Date.class)
                .apiInfo(new ApiInfoBuilder().title(SpringUtil.getApplicationName()).build())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(HttpServletResponse.class, HttpServletRequest.class)
                .securityContexts(securityContexts)
                .securitySchemes(securitySchemes);
    }

    private SecurityScheme securityScheme(SaTokenConfig saTokenConfig) {
        //简单模式 implicit
        /*LoginEndpoint loginEndpoint = new LoginEndpoint(swaggerClientProperties.getAuthCenterAddress());
        ImplicitGrant implicitGrant = new ImplicitGrant(loginEndpoint, "token");
        return new OAuthBuilder()
                .name("oauth2")
                .grantTypes(CollUtil.newArrayList(implicitGrant))
                .build();*/
        String tokenName = saTokenConfig == null ?
                SaManager.getConfig().getTokenName() : saTokenConfig.getTokenName();
        return new BasicAuth(tokenName);
    }

    private SecurityContext securityContext(SaTokenConfig saTokenConfig) {
        String tokenName = saTokenConfig == null ?
                SaManager.getConfig().getTokenName() : saTokenConfig.getTokenName();
        //SecurityReference securityReference = new SecurityReference("oauth2", new AuthorizationScope[]{});
        SecurityReference securityReference = new SecurityReference(tokenName, new AuthorizationScope[]{});
        return SecurityContext.builder()
                              .securityReferences(CollUtil.newArrayList(securityReference))
                              .build();
    }


}
