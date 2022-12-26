package tydic.framework.starter.innerRequest.template;

import com.tydic.framework.starter.innerRequest.model.InnerRequestResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultInnerRequestServerTemplate implements InnerRequestServerTemplate {
    @Override
    public void preInvoke(Object target, String methodName, Object[] args) {

    }

    @Override
    public void afterInvoke(Object target, String methodName, Object[] args, Object result) {

    }

    @Override
    public InnerRequestResult afterException(Exception e) {
        log.error("内部请求异常", e);
        return InnerRequestResult.error(e.getMessage());
    }
}
