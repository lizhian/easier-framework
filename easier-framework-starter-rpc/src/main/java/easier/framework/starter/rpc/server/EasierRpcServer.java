package easier.framework.starter.rpc.server;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.ZipUtil;
import easier.framework.core.Easier;
import easier.framework.core.plugin.innerRequest.InnerRequestException;
import easier.framework.core.plugin.rpc.RpcClient;
import easier.framework.core.plugin.rpc.RpcInterface;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.rpc.model.RpcRequestBody;
import easier.framework.starter.rpc.model.RpcResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EasierRpcServer {

    private final Map<String, Object> BEAN_CACHE = new ConcurrentHashMap<>();

    @Operation(hidden = true)
    @PostMapping(RpcClient.PATH)
    public byte[] req(@RequestBody byte[] bytes) {
        try {
            RpcRequestBody body = Easier.JsonTyped.toObject(ZipUtil.unGzip(bytes));
            String className = body.getClassName();
            String methodName = body.getMethodName();
            Object[] args = body.getArgs();
            if (args == null) {
                args = new Object[]{};
            }
            Object target = this.BEAN_CACHE.computeIfAbsent(className, this::findTargetBean);
            Method method = ReflectUtil.getMethodByName(target.getClass(), methodName);
            Object result = method.invoke(target, args);
            RpcResponseBody responseBody = RpcResponseBody.builder()
                    .success(true)
                    .result(result)
                    .build();
            return ZipUtil.gzip(Easier.JsonTyped.toJsonBytes(responseBody));
        } catch (Exception e) {
            Throwable error = e;
            if (e instanceof InvocationTargetException) {
                error = e.getCause();
            }
            log.error(error.getMessage(), error);
            RpcResponseBody responseBody = RpcResponseBody.builder()
                    .success(false)
                    .errorMessage(error.getMessage())
                    .build();
            return ZipUtil.gzip(Easier.JsonTyped.toJsonBytes(responseBody));
        }
    }

    private Object findTargetBean(String className) {
        RpcInterface target = SpringUtil.getBeanByClassName(RpcInterface.class, className);
        if (target == null) {
            throw new InnerRequestException("未找到实现类:" + className);
        }
        return target;
    }
}
