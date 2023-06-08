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
        this.setBlankProperty("" +
                "#token 名称\n" +
                "sa-token.token-name=tydic-token\n" +
                "\n" +
                "#token 前缀\n" +
                "sa-token.token-prefix=\n" +
                "\n" +
                "#token 有效期 30 天\n" +
                "sa-token.timeout=2592000\n" +
                "\n" +
                "#token 临时有效期 6 小时\n" +
                "sa-token.activity-timeout=21600\n" +
                "\n" +
                "#是否自动续签\n" +
                "sa-token.auto-renew=true\n" +
                "\n" +
                "#是否允许同一账号并发登录\n" +
                "sa-token.is-concurrent=true\n" +
                "\n" +
                "#多人登录是否共用一个token\n" +
                "sa-token.is-share=false\n" +
                "\n" +
                "#同一账号最大登录数量\n" +
                "sa-token.max-login-count=20\n" +
                "\n" +
                "#token 来源\n" +
                "sa-token.is-read-body=true\n" +
                "sa-token.is-read-header=true\n" +
                "sa-token.is-read-cookie=true\n" +
                "\n" +
                "#token 风格\n" +
                "sa-token.token-style=simple-uuid\n" +
                "\n" +
                "#获取 Token-Session 时是否必须登录\n" +
                "sa-token.token-session-check-login=true\n" +
                "\n" +
                "#是否在初始化配置时打印版本字符画\n" +
                "sa-token.is-print=true\n" +
                "\n" +
                "#是否打印日志\n" +
                "sa-token.is-log=false\n" +
                "\n" +
                "#是否禁止 js 操作 Cookie\n" +
                "sa-token.cookie.http-only=true");
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
