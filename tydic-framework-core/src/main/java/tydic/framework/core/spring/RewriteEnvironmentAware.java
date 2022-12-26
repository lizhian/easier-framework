package tydic.framework.core.spring;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import tydic.framework.core.util.SpringUtil;

public interface RewriteEnvironmentAware extends EnvironmentAware {
    default void setBlankProperty(String key, String value) {
        Logger log = LoggerFactory.getLogger(this.getClass());
        String property = SpringUtil.getProperty(key);
        if (StrUtil.isBlank(property) && StrUtil.isNotBlank(value)) {
            System.setProperty(key, value);
            log.info("写入默认配置【{}={}】", key, value);
        }
    }

    default void setBlankProperty(String defaultProperty) {
        if (StrUtil.isBlank(defaultProperty)) {
            return;
        }
        defaultProperty.lines()
                       .filter(str -> !str.startsWith("#"))
                       .filter(str -> str.contains("="))
                       .forEach(str -> {
                           String key = StrUtil.subBefore(str, "=", false);
                           String value = StrUtil.subAfter(str, "=", false);
                           this.setBlankProperty(key, value);
                       });

    }
}
