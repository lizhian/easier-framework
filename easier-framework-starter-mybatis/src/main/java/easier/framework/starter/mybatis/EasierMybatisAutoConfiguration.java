package easier.framework.starter.mybatis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourcePropertiesCustomizer;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.mybatis.template.DefaultEasierMybatisTemplate;
import easier.framework.starter.mybatis.template.EasierMybatisTemplate;
import easier.framework.starter.mybatis.types.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 更简单mybatis自动配置
 *
 * @author lizhian
 * @date 2023年07月05日
 */
@Slf4j
@EnableConfigurationProperties(EasierMybatisProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
@EnableTransactionManagement
public class EasierMybatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EasierMybatisTemplate easierMybatisTemplate() {
        return new DefaultEasierMybatisTemplate();
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer(Environment environment) {
        return configuration -> {
            //重写枚举处理
            configuration.setDefaultEnumTypeHandler(EnumCodecTypeHandler.class);
            //增加对DateTime类型的支持
            configuration.getTypeHandlerRegistry().register(DateTimeTypeHandler.instance);
            configuration.getTypeHandlerRegistry().register(ListIntTypeHandler.instance);
            configuration.getTypeHandlerRegistry().register(ListLongTypeHandler.instance);
            configuration.getTypeHandlerRegistry().register(ListStringTypeHandler.instance);
        };
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(EasierMybatisTemplate mybatisTemplate) {
        //分页插件
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        //动态表名插件
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler(mybatisTemplate);
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        return interceptor;
    }

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
