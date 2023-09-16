package easier.framework.starter.auth;

import cn.hutool.extra.spring.EnableSpringUtil;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.auth.Interceptor.AuthHandlerInterceptor;
import easier.framework.starter.auth.dao.SaTokenDaoForRedissonClients;
import easier.framework.starter.auth.template.DefaultEasierAuthTemplate;
import easier.framework.starter.auth.template.EasierAuthTemplate;
import easier.framework.starter.cache.EnableEasierCache;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableSpringUtil
@EnableEasierCache
@Import({SaTokenDaoForRedissonClients.class})
public class EasierAuthAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        EasierAuthTemplate authTemplate = SpringUtil.getBeanDefaultNull(EasierAuthTemplate.class);
        if (authTemplate == null) {
            authTemplate = new DefaultEasierAuthTemplate();
        }
        AuthHandlerInterceptor interceptor = new AuthHandlerInterceptor(authTemplate);
        // 打开鉴权功能
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }

}
