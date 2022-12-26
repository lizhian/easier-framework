package tydic.framework.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import tydic.framework.core.plugin.jackson.annotation.JsonSerializeAlias;
import tydic.framework.core.util.StrUtil;
import tydic.framework.core.util.TraceIdUtil;

import java.io.Serializable;

/**
 * 响应信息
 */
@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder(toBuilder = true)
public class R<T> implements Serializable {

    @ApiModelProperty("响应编码")
    private int code;

    @ApiModelProperty("响应消息")
    @JsonSerializeAlias("msg")
    private String message;

    @ApiModelProperty("响应数据")
    private T data;

    @ApiModelProperty(value = "链路标识", hidden = true)
    private String traceId;

    @ApiModelProperty(value = "拓展属性", hidden = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object expandData;


    public static <T> R<T> of(int code, String message, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(data);
        r.setTraceId(TraceIdUtil.get());
        return r;
    }

    public static <T> R<T> of(RCode code) {
        return of(code.getValue(), code.getLabel(), null);
    }

    public static <T> R<T> of(RCode code, T data) {
        return of(code.getValue(), code.getLabel(), data);
    }

    public static <T> R<T> of(RCode code, T data, String message) {
        if (StrUtil.isNotBlank(message)) {
            return of(code.getValue(), message, data);
        } else {
            return of(code.getValue(), code.getLabel(), data);
        }
    }


    public static <T> R<T> success() {
        return of(RCode.success);
    }

    public static <T> R<T> success(T data) {
        return of(RCode.success, data);
    }

    public static <T> R<T> success(T data, String message) {
        return of(RCode.success, data, message);
    }

    public static <T> R<T> failed() {
        return of(RCode.failed);
    }

    public static <T> R<T> failed(RCode code) {
        return of(code);
    }

    public static <T> R<T> failed(String message) {
        return of(RCode.failed, null, message);
    }

    public static <T> R<T> failed(T data, String message) {
        return of(RCode.failed, data, message);
    }
}
