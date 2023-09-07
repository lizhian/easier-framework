package easier.framework.starter.mybatis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourcePropertiesCustomizer;
import easier.framework.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 更简单mybatis自动配置
 *
 * @author lizhian
 * @date 2023年07月05日
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class EasierDynamicDataSourceAutoConfiguration {
    @Bean
    public DynamicDataSourcePropertiesCustomizer dynamicDataSourcePropertiesCustomizer(EasierMybatisProperties easierMybatisProperties) {
        return properties -> {
            List<String> enableDB = StrUtil.smartSplit(easierMybatisProperties.getEnableDb());
            if (CollUtil.isEmpty(enableDB)) {
                return;
            }
            String primary = IterUtil.getFirstNoneNull(enableDB);
            properties.setPrimary(primary);
            List<String> disableDB = properties.getDatasource()
                    .keySet()
                    .stream()
                    .filter(key -> !enableDB.contains(key))
                    .collect(Collectors.toList());
            for (String db : disableDB) {
                properties.getDatasource().remove(db);
            }
            log.info("当前启用的数据库为:{},默认数据库为:[{}]", enableDB, primary);
        };
    }


}
