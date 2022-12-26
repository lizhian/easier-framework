package tydic.framework.core.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListUtil extends cn.hutool.core.collection.ListUtil {
    /**
     * 重复元素计算值
     */
    public static <T> List<T> getDuplicateElements(List<T> list) {
        return list.stream()
                   .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
                   .entrySet()
                   .stream()
                   .filter(entry -> entry.getValue() > 1)
                   .map(Map.Entry::getKey)
                   .collect(Collectors.toList());
    }
}
