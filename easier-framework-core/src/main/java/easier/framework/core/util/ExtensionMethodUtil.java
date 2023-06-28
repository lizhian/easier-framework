package easier.framework.core.util;


import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.List;

public class ExtensionMethodUtil {
    public static String _format(String template, Object... params) {
        return StrUtil.format(template, params);
    }

    public static boolean isBlank(CharSequence str) {
        return StrUtil.isBlank(str);
    }

    public static boolean isNotBlank(CharSequence str) {
        return StrUtil.isNotBlank(str);
    }

    public static String nullToEmpty(String str) {
        return str == null ? StrUtil.EMPTY : str;
    }

    public static <T> List<T> newArrayList(T t) {
        if (t == null) {
            return new ArrayList<>();
        }
        return CollUtil.newArrayList(t);
    }


}
