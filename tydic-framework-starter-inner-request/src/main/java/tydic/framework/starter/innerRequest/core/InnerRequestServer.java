package tydic.framework.starter.innerRequest.core;

import cn.hutool.core.util.ReflectUtil;
import com.tydic.framework.starter.innerRequest.model.InnerRequestBody;
import com.tydic.framework.starter.innerRequest.model.InnerRequestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tydic.framework.core.plugin.innerRequest.InnerRequest;
import tydic.framework.core.plugin.innerRequest.InnerRequestException;
import tydic.framework.core.util.HessianUtil;
import tydic.framework.core.util.InstanceUtil;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.innerRequest.template.InnerRequestServerTemplate;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 内部请求服务端
 */
@RestController
@RequiredArgsConstructor
public class InnerRequestServer {
    private final InnerRequestServerTemplate template;

    @PostMapping(InnerRequest.PATH)
    public byte[] req(@RequestBody byte[] bodyBytes, HttpServletResponse response) {
        try {
            InnerRequestBody body = HessianUtil.deserialize(bodyBytes);
            String className = body.getClassName();
            String methodName = body.getMethodName();
            Object[] args = body.getArgs();
            if (args == null) {
                args = new Object[]{};
            }
            //获取对应类
            Object target = InstanceUtil.in(InnerRequestServer.class)
                                        .getInstance(className, this::findTargetBean);

            //前置处理
            this.template.preInvoke(target, methodName, args);
            //执行方法
            Method method = ReflectUtil.getMethodByName(target.getClass(), methodName);
            Object result = method.invoke(target, args);
            //后置处理
            this.template.afterInvoke(target, methodName, args, result);
            return HessianUtil.serialize(InnerRequestResult.success(result));
        } catch (Exception e) {
            //异常处理
            InnerRequestResult errorResult = this.template.afterException(e);
            response.setStatus(errorResult.getCode());
            return HessianUtil.serialize(errorResult);
        }
    }

    private Object findTargetBean(String className) {
        InnerRequest target = SpringUtil.getBeanByClassName(InnerRequest.class, className);
        if (target == null) {
            throw new InnerRequestException("未找到实现类:" + className);
        }
        return target;
    }
}
