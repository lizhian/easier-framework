package easier.framework.starter.auth;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.auth.Interceptor.AuthHandlerInterceptor;
import easier.framework.starter.auth.dao.SaTokenDaoForRedissonClients;
import easier.framework.starter.auth.template.DefaultEasierAuthTemplate;
import easier.framework.starter.auth.template.EasierAuthTemplate;
import easier.framework.starter.cache.EnableEasierCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableSpringUtil
@EnableEasierCache
@Import(SaTokenDaoForRedissonClients.class)
public class EasierAuthAutoConfiguration implements WebMvcConfigurer, InitializingBean {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        EasierAuthTemplate authTemplate = SpringUtil.getBean(EasierAuthTemplate.class);
        AuthHandlerInterceptor interceptor = new AuthHandlerInterceptor(authTemplate);
        //打开鉴权功能
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }

    @Bean
    @ConditionalOnMissingBean
    public EasierAuthTemplate easierAuthTemplate() {
        return new DefaultEasierAuthTemplate();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        /*//没权限异常
        ExceptionHandlerRegister.register(NotPermissionException.class, HttpStatus.FORBIDDEN, exception -> {
            R<Object> failed = R.failed(exception.getMessage());
            failed.setCode(RCode.not_permission.getValue());
            return failed;
        });
        //没角色异常
        ExceptionHandlerRegister.register(NotRoleException.class, HttpStatus.FORBIDDEN, exception -> {
            R<Object> failed = R.failed(exception.getMessage());
            failed.setCode(RCode.not_permission.getValue());
            return failed;
        });*/
    }
}
