package easier.framework.core.util;


import ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtil extends cn.hutool.core.util.ClassUtil {

    private static final TargetLengthBasedClassNameAbbreviator classNameMapper = new TargetLengthBasedClassNameAbbreviator(20);


    public String shortClassName(Class<?> clazz) {
        return classNameMapper.abbreviate(clazz.getName());
    }
}
