package easier.framework.core.util;


import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtensionCore {

    /**
     * 模板渲染
     *
     * @param template 模板
     * @param params   params
     * @return {@link String}
     */
    public static String _format(String template, Object... params) {
        return StrUtil.format(template, params);
    }

    public static boolean isBlank(CharSequence str) {
        return StrUtil.isBlank(str);
    }

    public static boolean isNotBlank(CharSequence str) {
        return StrUtil.isNotBlank(str);
    }

    public static <T> T or(T t, T other) {
        return t == null ? other : t;
    }

    public static String orEmpty(String str) {
        return str == null ? StrUtil.EMPTY : str;
    }

    public static <T> List<T> orEmpty(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }

    public static <K, V> Map<K, V> orEmpty(Map<K, V> map) {
        return map == null ? new HashMap<>() : map;
    }

    public static <T> List<T> asList(T t) {
        if (t == null) {
            return new ArrayList<>();
        }
        return CollUtil.newArrayList(t);
    }


}
