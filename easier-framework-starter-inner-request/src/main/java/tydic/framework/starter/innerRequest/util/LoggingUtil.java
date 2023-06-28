package easier.framework.starter.innerRequest.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

public class LoggingUtil {
    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }
        if (object instanceof Byte[] || object instanceof byte[]) {
            return StrUtil.format("byte[{}]", ArrayUtil.length(object));
        }
        String toString = object.toString();
        int length = StrUtil.length(toString);
        if (length > 100) {
            return StrUtil.subPre(toString, 100) + ".....[" + length + "]";
        }
        return toString;
    }
}
