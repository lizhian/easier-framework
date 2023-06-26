package tydic.framework.starter.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.extra.spring.EnableSpringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tydic.framework.core.domain.R;
import tydic.framework.core.domain.RCode;
import tydic.framework.core.plugin.exception.ExceptionHandlerRegister;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.auth.Interceptor.AuthHandlerInterceptor;
import tydic.framework.starter.auth.dao.SaTokenDaoForRedissonClients;
import tydic.framework.starter.auth.template.DefaultTydicAuthTemplate;
import tydic.framework.starter.auth.template.TydicAuthTemplate;
import tydic.framework.starter.cache.EnableTydicCache;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableSpringUtil
@EnableTydicCache
@Import(SaTokenDaoForRedissonClients.class)
public class TydicAuthAutoConfiguration implements WebMvcConfigurer, InitializingBean {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        TydicAuthTemplate authTemplate = SpringUtil.getBean(TydicAuthTemplate.class);
        AuthHandlerInterceptor interceptor = new AuthHandlerInterceptor(authTemplate);
        //打开鉴权功能
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }

    @Bean
    @ConditionalOnMissingBean
    public TydicAuthTemplate tydicAuthTemplate() {
        return new DefaultTydicAuthTemplate();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //未登录异常
        ExceptionHandlerRegister.register(NotLoginException.class, HttpStatus.UNAUTHORIZED, exception -> {
            R<Object> failed = R.failed(exception.getMessage());
            failed.setCode(RCode.not_login.getValue());
            exception.printStackTrace();
            return failed;
        });
        //没权限异常
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
        });
    }
}
