package tydic.framework.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tydic.framework.core.plugin.enums.Dict;
import tydic.framework.core.plugin.enums.EnumDesc;

/**
 * 通用启用状态
 */
@Getter
@RequiredArgsConstructor
@Dict(type = "enable:status", name = "通用启用状态")
public enum EnableStatus {
    enable("enable-1", "启用"),
    disable("disable-0", "停用");
    @EnumValue
    private final String value;
    @EnumDesc
    private final String label;
}
