package easier.framework.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import easier.framework.core.plugin.dict.Dict;
import easier.framework.core.plugin.enums.EnumDesc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Dict(code = "sex_type", name = "性别")
public enum SexType {
    man("1", "男"),
    woman("0", "女"),
    unknown("-1", "未知");
    @EnumValue
    private final String value;
    @EnumDesc
    private final String desc;
}
