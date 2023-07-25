package easier.framework.starter.mybatis;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourcePropertiesCustomizer;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.github.yulichang.toolkit.TableHelper;
import com.tangzc.mpe.base.event.InitScanEntityEvent;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.mybatis.interceptor.MybatisTimerInterceptor;
import easier.framework.starter.mybatis.template.DefaultEasierMybatisTemplate;
import easier.framework.starter.mybatis.template.EasierMybatisTemplate;
import easier.framework.starter.mybatis.types.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerGroup;
import org.springframework.boot.logging.LoggerGroups;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.Map;
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
    public ConfigurationCustomizer configurationCustomizer() {
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
    public MybatisTimerInterceptor mybatisTimerInterceptor() {
        return new MybatisTimerInterceptor();
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

    @EventListener
    public void bindMapperLoggerGroups(InitScanEntityEvent event) {
        String sql = "sql";
        LoggerGroups groups = SpringUtil.getBean(LoggingApplicationListener.LOGGER_GROUPS_BEAN_NAME);
        LoggerGroup sqlGroup = groups.get(sql);
        if (sqlGroup == null) {
            return;
        }
        String currentNamespace = TableHelper.get(event.getEntityClass()).getCurrentNamespace();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logbackLogger = loggerContext.getLogger(currentNamespace);
        if (logbackLogger.getLevel() != null) {
            return;
        }
        LogLevel configuredLevel = sqlGroup.getConfiguredLevel();
        logbackLogger.setLevel(Level.toLevel(configuredLevel.name()));
        log.info("修改日志等级为 {} -> {}", configuredLevel, currentNamespace);
        List<String> newMembers = CollUtil.newArrayList(sqlGroup.getMembers());
        newMembers.add(currentNamespace);
        Map<String, List<String>> namesAndMembers = MapUtil.builder(sql, newMembers).build();
        groups.putAll(namesAndMembers);
        groups.get(sql).configureLogLevel(configuredLevel, (a, b) -> {
        });

    }

}
