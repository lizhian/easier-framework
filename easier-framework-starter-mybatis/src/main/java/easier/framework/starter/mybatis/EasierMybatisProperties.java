package easier.framework.starter.mybatis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 更简单mybatis属性
 *
 * @author lizhian
 * @date 2023年07月05日
 */
@Getter
@Setter
@ConfigurationProperties(prefix = EasierMybatisProperties.PREFIX)
public class EasierMybatisProperties {

    public static final String PREFIX = "spring.datasource.dynamic";

    /**
     * 使用的数据库,第一个为主库
     */
    private String enableDb;

    private Boolean enabled;
}
