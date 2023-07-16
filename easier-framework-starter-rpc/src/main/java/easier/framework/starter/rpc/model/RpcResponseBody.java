package easier.framework.starter.rpc.model;

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
public class RpcResponseBody implements Serializable {
    //响应结果编码
    private boolean success;
    //异常详情
    private String errorMessage;
    //响应结果
    private Object result;

    public static RpcResponseBody success(Object result) {
        return RpcResponseBody.builder()
                .success(true)
                .result(result)
                .build();
    }
}
