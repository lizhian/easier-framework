package easier.framework.starter.doc.customizer;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ArrayUtil;
import com.github.xiaoymin.knife4j.core.conf.ExtensionsConstants;
import easier.framework.core.plugin.auth.annotation.*;
import easier.framework.core.plugin.enums.EnumCodec;
import easier.framework.core.util.ExtensionMethodUtil;
import easier.framework.core.util.StrUtil;
import io.swagger.v3.oas.models.Operation;
import lombok.experimental.ExtensionMethod;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.List;

@ExtensionMethod(ExtensionMethodUtil.class)
public class EasierOperationCustomizer implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        List<String> descriptions = operation.getDescription()
                .nullToEmpty()
                .newArrayList();
        forOrder(operation, handlerMethod);
        forMustPermission(descriptions, handlerMethod);
        forMustRole(descriptions, handlerMethod);
        forMustLogin(descriptions, handlerMethod);
        forIgnoreAuth(descriptions, handlerMethod);
        forBaseAuth(descriptions, handlerMethod);
        return operation.description(StrUtil.join(descriptions));
    }

    private void forOrder(Operation operation, HandlerMethod handlerMethod) {
        if (operation.getExtensions() != null && operation.getExtensions().containsKey(ExtensionsConstants.EXTENSION_ORDER)) {
            return;
        }
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequestMapping.class);
        if (requestMapping == null) {
            return;
        }
        RequestMethod requestMethod = ArrayUtil.firstNonNull(requestMapping.method());
        if (requestMethod == null) {
            return;
        }
        Integer index = EnumCodec.of(RequestMethod.class)
                .getEnumDetail(requestMethod)
                .getIndex();
        operation.addExtension(ExtensionsConstants.EXTENSION_ORDER, index);
    }

    private void forMustPermission(List<String> descriptions, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        MustPermission annotation = AnnotationUtil.getAnnotation(method, MustPermission.class);
        if (annotation == null) {
            return;
        }
        if (annotation.value().isBlank()) {
            return;
        }
        descriptions.add("接口需要权限编码【{}】"._format(annotation.value()));
    }

    private void forMustRole(List<String> descriptions, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        MustRole annotation = AnnotationUtil.getAnnotation(method, MustRole.class);
        if (annotation == null) {
            return;
        }
        if (annotation.value().isBlank()) {
            return;
        }
        descriptions.add("接口需要权限角色【{}】"._format(annotation.value()));
    }

    private void forIgnoreAuth(List<String> descriptions, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        IgnoreAuth annotation = AnnotationUtil.getAnnotation(method, IgnoreAuth.class);
        if (annotation == null) {
            return;
        }
        descriptions.add("接口不需要认证】");

    }

    private void forMustLogin(List<String> descriptions, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        MustLogin annotationInMethod = AnnotationUtil.getAnnotation(method, MustLogin.class);
        MustLogin annotationInClass = AnnotationUtil.getAnnotation(method.getDeclaringClass(), MustLogin.class);
        if (annotationInMethod == null || annotationInClass == null) {
            return;
        }
        descriptions.add("接口需要登录");
    }

    private void forBaseAuth(List<String> descriptions, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        BaseAuth baseAuth = AnnotationUtil.getAnnotation(method.getDeclaringClass(), BaseAuth.class);
        if (baseAuth == null || baseAuth.value().isBlank()) {
            return;
        }
        String baseAuthValue = baseAuth.value();

        if (AnnotationUtil.hasAnnotation(method, GetPermission.class)
                || AnnotationUtil.hasAnnotation(method, GetMapping.class)) {
            descriptions.add("接口需要基础查询权限编码【{}:{}】"._format(baseAuthValue, "Get"));
            return;
        }
        if (AnnotationUtil.hasAnnotation(method, DeletePermission.class)
                || AnnotationUtil.hasAnnotation(method, DeleteMapping.class)) {
            descriptions.add("接口需要基础删除权限编码【{}:{}】"._format(baseAuthValue, "Delete"));
            return;
        }
        descriptions.add("接口需要基础编辑权限编码【{}:{}】"._format(baseAuthValue, "Edit"));
    }

}
