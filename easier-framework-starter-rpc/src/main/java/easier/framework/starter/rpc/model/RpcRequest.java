package easier.framework.starter.rpc.model;

import easier.framework.core.plugin.rpc.enums.HostType;
import easier.framework.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求体
 */
@Data
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class RpcRequest implements Serializable {
    private final Method method;
    private final Object[] args;
    @Builder.Default
    private final List<String> traceMessages = new ArrayList<>();
    @Builder.Default
    private final Map<String, List<String>> headers = new HashMap<>();
    private final HostType type;
    private boolean https;
    private String host;
    private String path;
    private int connectionTimeout;
    private int readTimeout;
    private byte[] requestBodyBytes;
    private byte[] responseBodyBytes;
    private int responseStatus;
    private Object result;

    public void trace(String template, Object... params) {
        this.traceMessages.add(StrUtil.format(template, params));
    }
}
