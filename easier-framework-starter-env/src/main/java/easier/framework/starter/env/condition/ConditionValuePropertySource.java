package easier.framework.starter.env.condition;

import easier.framework.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.PropertySource;
import org.springframework.core.log.LogMessage;

import java.util.List;
import java.util.Map;

/**
 * 条件属性源，用于根据条件生成属性值
 */
@Slf4j
public class ConditionValuePropertySource extends PropertySource<Map<String, String>> {
    public static final String SOURCE_NAME = "condition";

    private static final String PREFIX = "condition.";

    /**
     * 构造函数
     */
    public ConditionValuePropertySource() {
        super(SOURCE_NAME);
    }

    /**
     * 根据属性名获取属性值
     *
     * @param name 属性名
     * @return 属性值
     */
    @Override
    public Object getProperty(String name) {
        if (!name.startsWith(PREFIX)) {
            return null;
        }
        this.logger.trace(LogMessage.format("生成条件属性值 for '%s'", name));
        Object conditionValue = this.getConditionValue(name.substring(PREFIX.length()));
        if (conditionValue != null) {
            log.info("配置转换【{}】结果【{}】", name, conditionValue);
        }
        return conditionValue;
    }

    /**
     * 获取条件值
     *
     * @param type 条件类型
     * @return 条件值
     */
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

    /**
     * 获取参数
     *
     * @param input 输入字符串
     * @return 参数值
     */
    private String getParam(String input) {
        String between = StrUtil.subBetween(input, "(", ")");
        return StrUtil.trim(between);
    }

    /**
     * 获取参数列表
     * @param input 输入字符串
     * @return 参数列表
     */
    private List<String> getParams(String input) {
        String param = this.getParam(input);
        return StrUtil.splitTrim(param, ", ");
    }

    /**
     * 获取then值
     * @param input 输入字符串
     * @return then值
     */
    private Object getThenValue(String input) {
        String thenValue = StrUtil.subAfter(input, "?", false);
        if (StrUtil.contains(thenValue, "!")) {
            thenValue = StrUtil.subBefore(thenValue, "!", false);
        }
        return StrUtil.trim(thenValue);
    }

    /**
     * 获取else值
     * @param input 输入字符串
     * @return else值
     */
    private Object getElseValue(String input) {
        String elseValue = StrUtil.subAfter(input, "!", false);
        if (StrUtil.isBlank(elseValue)) {
            return "";
        }
        return StrUtil.trim(elseValue);
    }

    /**
     * 判断参数是否为空，如果是则返回then值，否则返回else值
     * @param input 输入字符串
     * @return 判断结果
     */
    private Object isBlankValue(String input) {
        String param = this.getParam(input);
        if (StrUtil.isBlank(param)) {
            return this.getThenValue(input);
        }
        return this.getElseValue(input);
    }

    /**
     * 判断参数是否不为空，如果是则返回then值，否则返回else值
     * @param input 输入字符串
     * @return 判断结果
     */
    private Object isNotBlankValue(String input) {
        String param = StrUtil.subBetween(input, "(", ")");
        if (StrUtil.isNotBlank(param)) {
            return this.getThenValue(input);
        }
        return this.getElseValue(input);
    }

    /**
     * 判断两个参数是否相等，如果相等则返回then值，否则返回else值
     * @param input 输入字符串
     * @return 判断结果
     */
    private Object equalsValue(String input) {
        String param = StrUtil.subBetween(input, "(", ")");
        List<String> params = StrUtil.splitTrim(param, ",");
        if (StrUtil.equals(params.get(0), params.get(1))) {
            return this.getThenValue(input);
        }
        return this.getElseValue(input);
    }
}
