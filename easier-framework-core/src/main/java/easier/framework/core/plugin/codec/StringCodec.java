package easier.framework.core.plugin.codec;


import cn.hutool.core.util.CharsetUtil;
import easier.framework.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;

/**
 * 字符串编解码器
 *
 * @author lizhian
 * @date 2023年10月24日
 */
@RequiredArgsConstructor
public class StringCodec implements Codec<String> {
    public static final StringCodec GBK = new StringCodec(CharsetUtil.CHARSET_GBK);

    public static final StringCodec UTF_8 = new StringCodec(CharsetUtil.CHARSET_UTF_8);
    public static final StringCodec ISO_8859_1 = new StringCodec(CharsetUtil.CHARSET_ISO_8859_1);

    private final Charset charset;

    @Override
    public byte[] serialize(String value) {
        if (StrUtil.isBlank(value)) {
            return new byte[0];
        }
        return StrUtil.bytes(value, this.charset);
    }

    @Override
    public String deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return StrUtil.EMPTY;
        }
        return StrUtil.str(bytes, this.charset);
    }
}
