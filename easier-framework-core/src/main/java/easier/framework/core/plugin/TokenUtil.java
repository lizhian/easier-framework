package easier.framework.core.plugin;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class TokenUtil {
    public static void main(String[] args) {
        String token = generateToken("123456", "jstdf97NGd@$%_ph");
        System.out.println(token);
        System.out.println(parseToken(token, "jstdf97NGd@$%_ph"));
    }

    /**
     * 生成令牌
     *
     * @param account 账号/警号
     * @param aesKey  aes密钥
     * @return {@link String}
     */
    public static String generateToken(String account, String aesKey) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, aesKey.getBytes(StandardCharsets.UTF_8), aesKey.getBytes(StandardCharsets.UTF_8));
        return aes.encryptHex(account + "@@@" + new Date().getTime());
    }

    private static String parseToken(String token, String aesKey) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, aesKey.getBytes(StandardCharsets.UTF_8), aesKey.getBytes(StandardCharsets.UTF_8));
        return aes.decryptStr(token);
    }
}
