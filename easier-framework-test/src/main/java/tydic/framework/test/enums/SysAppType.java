package easier.framework.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import easier.framework.core.plugin.enums.Dict;
import easier.framework.core.plugin.enums.EnumDesc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 系统应用类型
 */
@Getter
@RequiredArgsConstructor
@Dict(type = "sys:app:type", name = "系统应用类型")
public enum SysAppType {
    inside("inside-1", "内部"),
    outside("outside-0", "外部");
    @EnumValue
    private final String value;
    @EnumDesc
    private final String desc;

}
