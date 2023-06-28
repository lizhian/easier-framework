package easier.framework.starter.mybatis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = MybatisDynamicProperties.PREFIX)
public class MybatisDynamicProperties {

    public static final String PREFIX = "spring.datasource.dynamic";

    /**
     * 使用的数据库,第一个为主库
     */
    private String enableDb;
}
