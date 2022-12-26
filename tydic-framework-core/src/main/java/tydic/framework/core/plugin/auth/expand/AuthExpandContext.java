package tydic.framework.core.plugin.auth.expand;

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
import java.util.stream.Collectors;

/**
 * 认证拓展器上下文
 */
@Data
@FieldNameConstants
@SuperBuilder(toBuilder = true)
public class AuthExpandContext {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HandlerMethod handlerMethod;
    private Method method;
    private Annotation annotation;
    private List<Annotation> allAuthAnnotations;

    public <A extends Annotation> boolean hasAnnotation(Class<A> annotationType) {
        return AnnotationUtil.hasAnnotation(this.method, annotationType)
                || AnnotationUtil.hasAnnotation(this.method.getDeclaringClass(), annotationType);
    }

    public <A extends Annotation> A gasAnnotation(Class<A> annotationType) {
        if (AnnotationUtil.hasAnnotation(this.method, annotationType)) {
            return AnnotationUtil.getAnnotation(this.method, annotationType);
        }
        return AnnotationUtil.getAnnotation(this.method.getDeclaringClass(), annotationType);
    }

    public List<Annotation> getOtherAuthAnnotation() {
        return this.allAuthAnnotations.stream()
                                      .filter(it -> !it.equals(this.annotation))
                                      .collect(Collectors.toList());
    }
}
