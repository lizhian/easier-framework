package easier.framework.core.plugin.auth.expand;

import cn.hutool.core.annotation.AnnotationUtil;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 认证拓展器上下文
 */
@Data
@FieldNameConstants
@SuperBuilder(toBuilder = true)
public class AuthExpandContext {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HandlerMethod handlerMethod;
    private final Method method;
    private final Annotation authAnnotation;
    private final List<Annotation> otherAuthAnnotations;

    public <A extends Annotation> boolean hasAnnotation(Class<A> annotationType) {
        return AnnotationUtil.hasAnnotation(this.method, annotationType)
                || AnnotationUtil.hasAnnotation(this.method.getDeclaringClass(), annotationType);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        if (AnnotationUtil.hasAnnotation(this.method, annotationType)) {
            return AnnotationUtil.getAnnotation(this.method, annotationType);
        }
        return AnnotationUtil.getAnnotation(this.method.getDeclaringClass(), annotationType);
    }
}
