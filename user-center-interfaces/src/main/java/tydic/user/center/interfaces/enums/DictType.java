package tydic.user.center.interfaces.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import easier.framework.core.plugin.dict.Dict;
import easier.framework.core.plugin.enums.EnumDesc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 字典类型
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Getter
@RequiredArgsConstructor
@Dict(code = "dict_type", name = "字典类型")
public enum DictType {
    enumDict("enum", "枚举字典"),
    bizDict("biz", "业务字典");
    @EnumValue
    private final String value;
    @EnumDesc
    private final String desc;

    public static boolean isEnumDict(DictType input) {
        return DictType.enumDict.equals(input);
    }

    public static boolean isBizDict(DictType input) {
        return DictType.bizDict.equals(input);
    }


}
