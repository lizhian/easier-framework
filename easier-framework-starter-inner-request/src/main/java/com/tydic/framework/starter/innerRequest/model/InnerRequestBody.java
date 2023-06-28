package com.easier.framework.starter.innerRequest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

/**
 * 请求体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class InnerRequestBody implements Serializable {
    //类名
    private String className;
    //方法名
    private String methodName;
    //参数
    private Object[] args;
}
