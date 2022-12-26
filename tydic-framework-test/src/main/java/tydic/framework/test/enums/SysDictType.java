package tydic.framework.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tydic.framework.core.plugin.enums.Dict;
import tydic.framework.core.plugin.enums.EnumLabel;

/**
 * 系统字典类型
 */
@Getter
@RequiredArgsConstructor
@Dict(type = "sys:dict:type", name = "系统字典类型")
public enum SysDictType {
    sys("sys", "系统字典"),
    biz("biz", "业务字典");
    @EnumValue
    private final String value;
    @EnumLabel
    private final String label;
}
