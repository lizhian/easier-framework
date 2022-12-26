package tydic.framework.core.plugin.mq;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.StandardException;
import tydic.framework.core.plugin.exception.BaseException;

@StandardException
public class MQBuilderException extends BaseException {


    public static MQBuilderException of(String template, Object... params) {
        return new MQBuilderException(StrUtil.format(template, params));
    }


}
