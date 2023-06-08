package tydic.framework.core.util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StrUtil extends cn.hutool.core.util.StrUtil {

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
        if (contains(src, "|")) {
            return splitTrim(src, "|")
                    .stream()
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
        }
        if (contains(src, ".")) {
            return splitTrim(src, ".")
                    .stream()
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
        }
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
