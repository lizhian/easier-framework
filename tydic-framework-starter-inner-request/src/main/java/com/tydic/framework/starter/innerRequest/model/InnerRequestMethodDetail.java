package com.tydic.framework.starter.innerRequest.model;

import lombok.Data;
import tydic.framework.core.plugin.innerRequest.ServiceDetail;
import tydic.framework.core.util.InstanceUtil;
import tydic.framework.starter.cache.builder.CacheBuilderInvoker;
import tydic.framework.starter.cache.builder.CacheMethodDetail;

import java.lang.reflect.Method;

/**
 * 方法详情
 */
@Data
public class InnerRequestMethodDetail {
    private final String className;
    private final String methodName;
    private final ServiceDetail serviceDetail;
    private final CacheMethodDetail cacheMethodDetail;

    public InnerRequestMethodDetail(Method method) {
        //类名
        this.className = method.getDeclaringClass().getName();
        //方法名
        this.methodName = method.getName();
        //获取类上的 ServiceDetail
        this.serviceDetail = method.getDeclaringClass().getAnnotation(ServiceDetail.class);
        this.cacheMethodDetail = InstanceUtil.in(CacheBuilderInvoker.class)
                                             .getInstance(method, CacheMethodDetail::new);
    }
}