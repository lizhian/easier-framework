package easier.framework.starter.env;

import easier.framework.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.PropertySource;
import org.springframework.core.log.LogMessage;

import java.util.List;
import java.util.Map;

@Slf4j
public class ConditionValuePropertySource extends PropertySource<Map<String, String>> {
    public static final String SOURCE_NAME = "condition";

    private static final String PREFIX = "condition.";


    public ConditionValuePropertySource() {
        super(SOURCE_NAME);
    }

    @Override
    public Object getProperty(String name) {
        if (!name.startsWith(PREFIX)) {
            return null;
        }
        this.logger.trace(LogMessage.format("Generating condition property for '%s'", name));
        Object conditionValue = this.getConditionValue(name.substring(PREFIX.length()));
        if (conditionValue != null) {
            log.info("配置转换 {} -> {}", name, conditionValue);
        }
        return conditionValue;
    }

    private Object getConditionValue(String type) {
        if (type.startsWith("isBlank")) {
            return this.isBlankValue(type.substring("isBlank".length()));
        }
        if (type.startsWith("isNotBlank")) {
            return this.isNotBlankValue(type.substring("isNotBlank".length()));
        }
        if (type.startsWith("equals")) {
            return this.equalsValue(type.substring("equals".length()));

        }
        return null;
    }

    private String getParam(String input) {
        String between = StrUtil.subBetween(input, "(", ")");
        return StrUtil.trim(between);
    }

    private List<String> getParams(String input) {
        String param = this.getParam(input);
        return StrUtil.splitTrim(param, ", ");
    }

    private Object getThenValue(String input) {
        String thenValue = StrUtil.subAfter(input, "?", false);
        if (StrUtil.contains(thenValue, "!")) {
            thenValue = StrUtil.subBefore(thenValue, "!", false);
        }
        return StrUtil.trim(thenValue);
    }

    private Object getElseValue(String input) {
        String elseValue = StrUtil.subAfter(input, "!", false);
        if (StrUtil.isBlank(elseValue)) {
            return "";
        }
        return StrUtil.trim(elseValue);
    }

    private Object isBlankValue(String input) {
        String param = this.getParam(input);
        if (StrUtil.isBlank(param)) {
            return this.getThenValue(input);
        }
        return this.getElseValue(input);
    }


    private Object isNotBlankValue(String input) {
        String param = StrUtil.subBetween(input, "(", ")");
        if (StrUtil.isNotBlank(param)) {
            return this.getThenValue(input);
        }
        return this.getElseValue(input);
    }

    private Object equalsValue(String input) {
        String param = StrUtil.subBetween(input, "(", ")");
        List<String> params = StrUtil.splitTrim(param, ",");
        if (StrUtil.equals(params.get(0), params.get(1))) {
            return this.getThenValue(input);
        }
        return this.getElseValue(input);
    }
}
