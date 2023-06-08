package tydic.framework.core.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.core.util.StrUtil;

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
        StrUtil.lines(defaultProperty)
                .map(String::trim)
                .filter(str -> !str.startsWith("#"))
                .filter(str -> str.contains("="))
                .forEach(str -> {
                    String key = StrUtil.subBefore(str, "=", false);
                    String value = StrUtil.subAfter(str, "=", false);
                    this.setBlankProperty(key, value);
                });

    }
}
