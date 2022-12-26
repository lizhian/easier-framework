package tydic.framework.starter.auth.Interceptor;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tydic.framework.core.plugin.auth.AuthContext;
import tydic.framework.core.plugin.auth.expand.AuthExpand;
import tydic.framework.core.plugin.auth.expand.AuthExpandContext;
import tydic.framework.core.plugin.auth.expand.AuthExpander;
import tydic.framework.core.util.InstanceUtil;
import tydic.framework.starter.auth.template.TydicAuthTemplate;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class AuthHandlerInterceptor implements HandlerInterceptor {

    private final TydicAuthTemplate authTemplate;

    @Override
    @ParametersAreNonnullByDefault
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return this.preHandlerMethod(request, response, handlerMethod);
        }
        return true;
    }

    private boolean preHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        Annotation[] annotations = ArrayUtil.append(method.getAnnotations(), method.getDeclaringClass().getAnnotations());
        List<Annotation> allAuthAnnotations = Arrays.stream(annotations)
                                                    .filter(annotation -> AnnotationUtil.hasAnnotation(annotation.annotationType(), AuthExpand.class))
                                                    .collect(Collectors.toList());
        this.beforeAuth();
        for (Annotation annotation : allAuthAnnotations) {
            AuthExpand authExpand = AnnotationUtil.getAnnotation(annotation.annotationType(), AuthExpand.class);
            for (Class<? extends AuthExpander<?>> expanderClass : authExpand.expandBy()) {
                AuthExpandContext context = AuthExpandContext.builder()
                                                             .request(request)
                                                             .response(response)
                                                             .handlerMethod(handlerMethod)
                                                             .method(method)
                                                             .annotation(annotation)
                                                             .allAuthAnnotations(allAuthAnnotations)
                                                             .build();
                AuthExpander expander = InstanceUtil.in(AuthHandlerInterceptor.class)
                                                    .getInstance(expanderClass, ReflectUtil::newInstance);
                expander.doExpand(annotation, context);
            }
        }
        return true;
    }

    private void beforeAuth() {
        try {
            if (AuthContext.isLogin()) {
                return;
            }
            String tokenValue = AuthContext.getTokenValue();
            if (StrUtil.isBlank(tokenValue)) {
                return;
            }
            String account = this.authTemplate.tokenToAccount(tokenValue);
            if (StrUtil.isBlank(account)) {
                return;
            }
            AuthContext.login(account);
        } catch (Exception ignored) {
        }
    }
}
