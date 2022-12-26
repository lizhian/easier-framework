package tydic.framework.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tydic.framework.core.plugin.enums.Dict;
import tydic.framework.core.plugin.enums.EnumLabel;

/**
 * 系统应用类型
 */
@Getter
@RequiredArgsConstructor
@Dict(type = "sys:app:type", name = "系统应用类型")
public enum SysAppType {
    inside("inside", "内部"),
    outside("outside", "外部");
    @EnumValue
    private final String value;
    @EnumLabel
    private final String label;

}
