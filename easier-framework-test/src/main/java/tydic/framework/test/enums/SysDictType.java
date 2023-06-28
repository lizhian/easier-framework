package easier.framework.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import easier.framework.core.plugin.enums.Dict;
import easier.framework.core.plugin.enums.EnumDesc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    @EnumDesc
    private final String desc;
}
