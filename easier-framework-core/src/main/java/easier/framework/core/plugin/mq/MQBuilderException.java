package easier.framework.core.plugin.mq;

import cn.hutool.core.util.StrUtil;
import easier.framework.core.plugin.exception.BaseException;
import lombok.experimental.StandardException;

@StandardException
public class MQBuilderException extends BaseException {


    public static MQBuilderException of(String template, Object... params) {
        return new MQBuilderException(StrUtil.format(template, params));
    }


}
