package tydic.user.center.interfaces.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import easier.framework.core.plugin.dict.Dict;
import easier.framework.core.plugin.enums.EnumDesc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 性别类型
 *
 * @author lizhian
 * @date 2023年07月12日
 */
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
