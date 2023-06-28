package easier.framework.core.domain;

import com.baomidou.mybatisplus.annotation.EnumValue;
import easier.framework.core.plugin.enums.Dict;
import easier.framework.core.plugin.enums.EnumDesc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 相应编码
 */
@Getter
@RequiredArgsConstructor
@Dict(type = "r_code", name = "操作类型")
public enum RCode {
    success(200, "操作成功"),
    failed(400, "操作失败"),
    not_login(401, "未登录"),
    not_permission(403, "无权进行此操作"),
    not_found(404, "未找到相关资源"),
    ;
    @EnumValue
    private final int value;
    @EnumDesc
    private final String label;
}
