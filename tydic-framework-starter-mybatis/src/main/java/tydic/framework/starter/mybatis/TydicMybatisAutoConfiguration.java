package tydic.framework.starter.mybatis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourcePropertiesCustomizer;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.tangzc.mpe.actable.EnableAutoTable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import tydic.framework.core.spring.RewriteEnvironmentAware;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.mybatis.init.InitEntityTable;
import tydic.framework.starter.mybatis.relatedDelete.RelatedDeleteManager;
import tydic.framework.starter.mybatis.template.DefaultTydicMybatisTemplate;
import tydic.framework.starter.mybatis.template.TydicMybatisTemplate;
import tydic.framework.starter.mybatis.types.DateTimeTypeHandler;
import tydic.framework.starter.mybatis.types.EnumCodecTypeHandler;

import java.util.List;

@Slf4j
@EnableConfigurationProperties(MybatisDynamicProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableSpringUtil
@EnableAutoTable
@Import({RelatedDeleteManager.class, InitEntityTable.class})
public class TydicMybatisAutoConfiguration implements RewriteEnvironmentAware {

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.setBlankProperty("""
                #索引前缀
                actable.index.prefix=idx_
                                
                #唯一索引前缀
                actable.unique.prefix=uni_
                                
                #是否开启Mybatis二级缓存
                mybatis-plus.configuration.cache-enabled=false
                                
                #表名是否使用驼峰转下划线命名，只对表名生效
                mybatis-plus.global-config.db-config.table-underline=true
                                
                #是否开启自动驼峰命名规则（camel case）映射
                mybatis-plus.configuration.map-underscore-to-camel-case=true
                """);
    }


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
                                               .toList();
            for (String db : disableDB) {
                properties.getDatasource().remove(db);
            }
            log.info("当前启用的数据库为:{},默认数据库为:[{}]", enableDB, primary);
        };
    }

}
