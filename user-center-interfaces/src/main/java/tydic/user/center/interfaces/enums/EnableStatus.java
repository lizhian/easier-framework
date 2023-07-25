package tydic.user.center.interfaces.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import easier.framework.core.plugin.dict.Dict;
import easier.framework.core.plugin.enums.EnumDesc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 启用状态
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Getter
@FieldNameConstants
@RequiredArgsConstructor
@Dict(code = "enable_status", name = "通用启用状态")
public enum EnableStatus {
    enable("enable", "启用"),
    disable("disable", "停用");
    public static final String defaultValue = "enable";
    @EnumValue
    private final String value;
    @EnumDesc
    private final String desc;

    public static boolean isEnable(EnableStatus status) {
        return EnableStatus.enable.equals(status);
    }

    public static boolean isDisable(EnableStatus status) {
        return !EnableStatus.enable.equals(status);
    }

}
