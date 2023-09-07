package easier.framework.core.util;

import cn.hutool.core.util.StrUtil;
import com.plumelog.core.TraceId;

public class TraceIdUtil {

    public static final String key_trace_id = "x-trace-id";
    // public static String key_trace_id = "traceId";

    public static String get() {
        return TraceId.logTraceID.get();
    }

    public static String getOrDefault(String defaultVale) {
        String traceId = TraceId.logTraceID.get();
        if (StrUtil.isBlank(traceId)) {
            return defaultVale;
        }
        return traceId;
    }

    public static String getOrCreate() {
        String traceId = TraceId.logTraceID.get();
        if (StrUtil.isBlank(traceId)) {
            return create();
        }
        return traceId;
    }

    public static String create() {
        TraceId.logTraceID.set(IdUtil.nextIdStr());
        return TraceId.logTraceID.get();
    }

    public static void set(String traceId) {
        TraceId.logTraceID.set(traceId);
    }

    /**
     * 禁止输出和收集日志
     */
    public static void disable() {
        TraceId.logTraceID.set("-1");
    }

    public static boolean isDisable() {
        String traceId = get();
        return "-1".equals(traceId);
    }
}
