package tydic.framework.starter.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.extra.spring.EnableSpringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tydic.framework.core.domain.R;
import tydic.framework.core.domain.RCode;
import tydic.framework.core.plugin.exception.ExceptionHandlerRegister;
import tydic.framework.core.spring.RewriteEnvironmentAware;
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
public class TydicAuthAutoConfiguration implements WebMvcConfigurer, RewriteEnvironmentAware {

    @Override
    public void setEnvironment(Environment environment) {
        this.setBlankProperty("""
                #token 名称
                sa-token.token-name=tydic-token
                                
                #token 前缀
                sa-token.token-prefix=
                                
                #token 有效期 30 天
                sa-token.timeout=2592000
                                
                #token 临时有效期 6 小时
                sa-token.activity-timeout=21600
                                
                #是否自动续签
                sa-token.auto-renew=true
                                
                #是否允许同一账号并发登录
                sa-token.is-concurrent=true
                                
                #多人登录是否共用一个token
                sa-token.is-share=false
                                
                #同一账号最大登录数量
                sa-token.max-login-count=20
                                
                #token 来源
                sa-token.is-read-body=true
                sa-token.is-read-header=true
                sa-token.is-read-cookie=true
                                
                #token 风格
                sa-token.token-style=simple-uuid
                                
                #获取 Token-Session 时是否必须登录
                sa-token.token-session-check-login=true
                                
                #是否在初始化配置时打印版本字符画
                sa-token.is-print=true
                                
                #是否打印日志
                sa-token.is-log=false
                                
                #是否禁止 js 操作 Cookie
                sa-token.cookie.http-only=true
                """);
        this.registerExceptionHandler();
    }


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


    private void registerExceptionHandler() {
        //未登录异常
        ExceptionHandlerRegister.register(NotLoginException.class, HttpStatus.UNAUTHORIZED, exception -> {
            R<Object> failed = R.failed(exception.getMessage());
            failed.setCode(RCode.not_login.getValue());
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
