package easier.framework.starter.env;

import cn.hutool.core.lang.Console;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import easier.framework.core.util.StrUtil;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptUtil {
    private static final JasyptEncryptorConfigurationProperties properties = new JasyptEncryptorConfigurationProperties();


    public static void main(String[] args) {
        encryptConsole("1234567890", "123456");
    }

    public static void encryptConsole(String password, String content) {
        String encrypt = encrypt(password, content);
        Console.log("{}加密\n{}\n", content, encrypt);
    }

    public static void decryptConsole(String password, String content) {
        String decrypt = decrypt(password, content);
        Console.log("{}解密\n{}\n", content, decrypt);
    }

    public static String encrypt(String password, String content) {
        String prefix = properties.getProperty().getPrefix();
        String suffix = properties.getProperty().getSuffix();
        StringEncryptor encryptor = defaultEncryptor(password);
        return prefix + encryptor.encrypt(content) + suffix;
    }


    public static String decrypt(String password, String content) {
        String prefix = properties.getProperty().getPrefix();
        String suffix = properties.getProperty().getSuffix();
        if (content.startsWith(prefix)) {
            content = StrUtil.removePrefix(content, prefix);
            content = StrUtil.removeSuffix(content, suffix);
        }
        StringEncryptor encryptor = defaultEncryptor(password);
        return encryptor.decrypt(content);
    }


    private static StringEncryptor defaultEncryptor(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setAlgorithm(properties.getAlgorithm());
        config.setPassword(password);
        config.setStringOutputType(properties.getStringOutputType());
        config.setKeyObtentionIterations(properties.getKeyObtentionIterations());
        config.setSaltGeneratorClassName(properties.getSaltGeneratorClassname());
        config.setIvGeneratorClassName(properties.getIvGeneratorClassname());
        config.setPoolSize(properties.getPoolSize());
        config.setProviderClassName(properties.getProviderClassName());
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setConfig(config);
        encryptor.initialize();
        return encryptor;
    }
}
