package tydic.framework.starter.mybatis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourcePropertiesCustomizer;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.mybatis.template.DefaultTydicMybatisTemplate;
import tydic.framework.starter.mybatis.template.TydicMybatisTemplate;
import tydic.framework.starter.mybatis.types.DateTimeTypeHandler;
import tydic.framework.starter.mybatis.types.EnumCodecTypeHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@EnableConfigurationProperties(MybatisDynamicProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
public class TydicMybatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TydicMybatisTemplate tydicMybatisTemplate() {
        return new DefaultTydicMybatisTemplate();
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer(Environment environment) {
        return configuration -> {
            //重写枚举处理
            configuration.setDefaultEnumTypeHandler(EnumCodecTypeHandler.class);
            //增加对DateTime类型的支持
            configuration.getTypeHandlerRegistry().register(DateTimeTypeHandler.instance);
        };
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(TydicMybatisTemplate mybatisTemplate) {
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
    public DynamicDataSourcePropertiesCustomizer dynamicDataSourcePropertiesCustomizer(MybatisDynamicProperties dynamicProperties) {
        return properties -> {
            List<String> enableDB = StrUtil.smartSplit(dynamicProperties.getEnableDb());
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
