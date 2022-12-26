package tydic.framework.core.plugin.innerRequest;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.StandardException;
import tydic.framework.core.plugin.exception.BaseException;

@StandardException
public class InnerRequestException extends BaseException {


    public static InnerRequestException of(String template, Object... params) {
        return new InnerRequestException(StrUtil.format(template, params));
    }


}
