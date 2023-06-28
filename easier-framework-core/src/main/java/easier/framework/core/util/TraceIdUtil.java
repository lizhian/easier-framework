package easier.framework.core.util;

import cn.hutool.core.util.StrUtil;
import com.plumelog.core.TraceId;

public class TraceIdUtil {

    public static String traceId = "traceId";

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
        TraceId.set();
        return TraceId.logTraceID.get();
    }

    public static void set(String traceId) {
        TraceId.logTraceID.set(traceId);
    }
}
