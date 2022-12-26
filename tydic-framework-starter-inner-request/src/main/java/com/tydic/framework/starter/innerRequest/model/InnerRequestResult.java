package com.tydic.framework.starter.innerRequest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

/**
 * 响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class InnerRequestResult implements Serializable {
    //响应结果编码
    private Integer code;
    //异常详情
    private String exceptionDetail;
    //响应结果
    private Object result;


    public static InnerRequestResult error(String exceptionDetail) {
        return InnerRequestResult.builder()
                .code(400)
                .exceptionDetail(exceptionDetail)
                .build();
    }

    public static InnerRequestResult error(Integer code, String exceptionDetail) {
        return InnerRequestResult.builder()
                .code(code)
                .exceptionDetail(exceptionDetail)
                .build();
    }


    public static InnerRequestResult success(Object result) {
        return InnerRequestResult.builder()
                .code(200)
                .result(result)
                .build();
    }
}
