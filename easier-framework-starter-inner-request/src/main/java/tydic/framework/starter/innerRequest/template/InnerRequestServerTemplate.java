package easier.framework.starter.innerRequest.template;


import com.easier.framework.starter.innerRequest.model.InnerRequestResult;

public interface InnerRequestServerTemplate {

    /**
     * 前置处理
     */
    void preInvoke(Object target, String methodName, Object[] args);

    /**
     * 后置处理
     */
    void afterInvoke(Object target, String methodName, Object[] args, Object result);

    /**
     * 异常处理
     */
    InnerRequestResult afterException(Exception e);
}
