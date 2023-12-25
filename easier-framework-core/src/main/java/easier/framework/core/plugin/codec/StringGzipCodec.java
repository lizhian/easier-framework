package easier.framework.core.plugin.codec;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import easier.framework.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;

/**
 * gzip字符串编解码器
 *
 * @author lizhian
 * @date 2023年10月24日
 */
@RequiredArgsConstructor
public class StringGzipCodec implements Codec<String> {

    public static final StringGzipCodec GBK = new StringGzipCodec(CharsetUtil.CHARSET_GBK);

    public static final StringGzipCodec UTF_8 = new StringGzipCodec(CharsetUtil.CHARSET_UTF_8);
    public static final StringGzipCodec ISO_8859_1 = new StringGzipCodec(CharsetUtil.CHARSET_ISO_8859_1);

    private final Charset charset;

    @Override
    public byte[] serialize(String value) {
        if (StrUtil.isBlank(value)) {
            return new byte[0];
        }
        return ZipUtil.gzip(value, this.charset.name());
    }

    @Override
    public String deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return StrUtil.EMPTY;
        }
        return ZipUtil.unGzip(bytes, this.charset.name());
    }
}
