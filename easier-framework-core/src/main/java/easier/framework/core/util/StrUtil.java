package easier.framework.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StrUtil extends cn.hutool.core.util.StrUtil {

    public static String join(List<String> list) {
        if (CollUtil.isEmpty(list)) {
            return StrUtil.EMPTY;
        }
        return list.stream()
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining(StrPool.COMMA));
    }

    public static boolean isNotBlankIfStr(Object obj) {
        if (obj instanceof CharSequence) {
            return isNotBlank((CharSequence) obj);
        }
        return obj != null;
    }

    @Nonnull
    public static List<String> smartSplit(String src) {
        if (StrUtil.isBlank(src)) {
            return new ArrayList<>();
        }
        if (contains(src, ",")) {
            return splitTrim(src, ",")
                    .stream()
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
        }
        if (contains(src, ";")) {
            return splitTrim(src, ";")
                    .stream()
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
        }
        /*if (contains(src, "|")) {
            return splitTrim(src, "|")
                    .stream()
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
        }*/
        /*if (contains(src, ".")) {
            return splitTrim(src, ".")
                    .stream()
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
        }*/
        return StrUtil.lines(src)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }

    public static Stream<String> lines(String src) {
        if (src == null) {
            return Stream.of();
        }
        return StrUtil.splitTrim(src, "\n")
                .stream();
    }
}
