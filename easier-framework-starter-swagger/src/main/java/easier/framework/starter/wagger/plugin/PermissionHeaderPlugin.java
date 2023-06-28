package easier.framework.starter.wagger.plugin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import easier.framework.core.plugin.auth.annotation.DeletePermission;
import easier.framework.core.plugin.auth.annotation.GetPermission;
import easier.framework.core.plugin.auth.annotation.MustPermission;
import easier.framework.core.plugin.auth.annotation.MustRole;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

@Order
@RequiredArgsConstructor
public class PermissionHeaderPlugin implements OperationBuilderPlugin {

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

    @Override
    public void apply(OperationContext context) {
        String mustPermission = context.findAnnotation(MustPermission.class)
                                       .map(MustPermission::value)
                                       .orElse("");
        if (StrUtil.isNotBlank(mustPermission)) {
            this.addHeader(context, "用户缺少权限:【{}】", mustPermission);
            return;
        }
        String mustRole = context.findAnnotation(MustRole.class)
                                 .map(MustRole::value)
                                 .orElse("");
        if (StrUtil.isNotBlank(mustPermission)) {
            this.addHeader(context, "用户缺少角色:【{}】 ", mustRole);
            return;
        }
        String routePath = context.findControllerAnnotation(Api.class)
                                  .map(Api::value)
                                  .orElse("");
        if (StrUtil.isBlank(routePath)) {
            return;
        }
        String methodType = this.loadMethodType(context);
        if (StrUtil.isBlank(methodType)) {
            return;
        }
        this.addHeader(context, "用户缺少权限:【{}#{}】", routePath, methodType);
    }

    private void addHeader(OperationContext context, String template, Object... params) {
        String message = StrUtil.format(template, params);
        ResponseMessage responseMessage = new ResponseMessageBuilder()
                .code(403)
                .message(message)
                .build();
        context.operationBuilder()
               .responseMessages(CollUtil.newHashSet(responseMessage));
    }


    private String loadMethodType(OperationContext context) {
        if (context == null) {
            return null;
        }
        if (context.findAnnotation(GetPermission.class).isPresent() || context.findAnnotation(GetMapping.class).isPresent()) {
            return "Get";
        }
        if (context.findAnnotation(DeletePermission.class).isPresent() || context.findAnnotation(DeleteMapping.class).isPresent()) {
            return "Delete";
        }
        if (context.findAnnotation(PutMapping.class).isPresent() || context.findAnnotation(PostMapping.class).isPresent()) {
            return "Edit";
        }

        RequestMapping requestMapping = context.findAnnotation(RequestMapping.class).orElse(null);
        if (requestMapping == null) {
            return null;
        }
        RequestMethod requestMethod = ArrayUtil.firstNonNull(requestMapping.method());
        if (requestMethod == null) {
            return null;
        }
        if (RequestMethod.GET.equals(requestMethod)) {
            return "Get";
        }
        if (RequestMethod.DELETE.equals(requestMethod)) {
            return "Delete";
        }
        if (RequestMethod.PUT.equals(requestMethod) || RequestMethod.POST.equals(requestMethod)) {
            return "Edit";
        }
        return null;
    }
}


