package easier.framework.starter.auth.Interceptor;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import easier.framework.core.plugin.auth.AuthContext;
import easier.framework.core.plugin.auth.expand.AuthExpand;
import easier.framework.core.plugin.auth.expand.AuthExpandContext;
import easier.framework.core.plugin.auth.expand.AuthExpander;
import easier.framework.core.util.InstanceUtil;
import easier.framework.starter.auth.template.EasierAuthTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

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

    private final EasierAuthTemplate authTemplate;

    @Override
    @ParametersAreNonnullByDefault
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            this.preHandlerMethod(request, response, handlerMethod);
            return true;
        }
        log.info("handler {}", handler);
        return true;
    }

    private void preHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        this.beforeAuth();
        Annotation[] annotations = ArrayUtil.append(method.getAnnotations(), method.getDeclaringClass().getAnnotations());
        if (annotations == null) {
            return;
        }
        List<Annotation> allAuthAnnotations = Arrays.stream(annotations)
                .filter(annotation -> AnnotationUtil.hasAnnotation(annotation.annotationType(), AuthExpand.class))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(allAuthAnnotations)) {
            return;
        }
        for (Annotation authAnnotation : allAuthAnnotations) {
            List<Annotation> otherAuthAnnotations = Arrays.stream(annotations)
                    .filter(annotation -> AnnotationUtil.hasAnnotation(annotation.annotationType(), AuthExpand.class))
                    .filter(it -> !it.equals(authAnnotation))
                    .collect(Collectors.toList());
            AuthExpandContext context = AuthExpandContext.builder()
                    .request(request)
                    .response(response)
                    .handlerMethod(handlerMethod)
                    .method(method)
                    .authAnnotation(authAnnotation)
                    .otherAuthAnnotations(otherAuthAnnotations)
                    .build();
            this.doAuth(context);
        }
    }

    private void doAuth(AuthExpandContext context) {
        Annotation authAnnotation = context.getAuthAnnotation();
        AuthExpand authExpand = AnnotationUtil.getAnnotation(authAnnotation.annotationType(), AuthExpand.class);
        for (Class<? extends AuthExpander<?>> expanderClass : authExpand.expandBy()) {
            AuthExpander expander = InstanceUtil.in(AuthHandlerInterceptor.class).getInstance(expanderClass, ReflectUtil::newInstance);
            expander.doExpand(authAnnotation, context);
        }
    }

    private void beforeAuth() {
        try {
            if (AuthContext.isLogin()) {
                return;
            }
            String tokenValue = AuthContext.getTokenValue();

            this.authTemplate.tryLogin(tokenValue);

        } catch (Exception ignored) {
        }
    }
}
