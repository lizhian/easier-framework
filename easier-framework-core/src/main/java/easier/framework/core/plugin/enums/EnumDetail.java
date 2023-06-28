package easier.framework.core.plugin.enums;

import cn.hutool.core.util.NumberUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class EnumDetail<E extends Enum<E>> {
    /**
     * 下标
     */
    private final Integer index;
    /**
     * 枚举实例
     */
    private final E instance;
    /**
     * 枚举值
     */
    private final Object value;
    /**
     * 是否是int值
     */
    private final boolean isIntValue;
    /**
     * 枚举描述
     */
    private final String description;

    /**
     * 字典类型
     */
    private final String dictType;
    /**
     * 字典名称
     */
    private final String dictName;

    /**
     * 字典属性1
     */
    private final String dictProperty1;

    /**
     * 字典属性2
     */
    private final String dictProperty2;

    /**
     * 字典属性3
     */
    private final String dictProperty3;


    public Integer getValueAsInt() {
        if (value != null) {
            return NumberUtil.parseInt(value.toString());
        }
        return null;
    }


    public String getValueAsStr() {
        if (value != null) {
            return value.toString();
        }
        return "";
    }
}
