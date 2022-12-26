package tydic.framework.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tydic.framework.core.plugin.enums.Dict;
import tydic.framework.core.plugin.enums.EnumLabel;

/**
 * 通用启用状态
 */
@Getter
@RequiredArgsConstructor
@Dict(type = "enable:status", name = "通用启用状态")
public enum EnableStatus {
    enable("enable", "启用"),
    disable("disable", "停用");
    @EnumValue
    private final String value;
    @EnumLabel
    private final String label;
}
