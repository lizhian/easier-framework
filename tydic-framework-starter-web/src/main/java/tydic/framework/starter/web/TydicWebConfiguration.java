package tydic.framework.starter.web;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tydic.framework.core.spring.RewriteEnvironmentAware;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.cache.EnableTydicCache;
import tydic.framework.starter.jackson.EnableTydicJackson;
import tydic.framework.starter.job.EnableTydicJob;
import tydic.framework.starter.web.converter.StringToEnumConverterFactory;
import tydic.framework.starter.web.converter.TimeConverters;
import tydic.framework.starter.web.filter.TraceIdServletFilter;
import tydic.framework.starter.web.handler.GlobalExceptionHandler;

import java.lang.management.ManagementFactory;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author tgd
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableTydicJackson
@EnableTydicCache
@EnableTydicJob
@Import(TraceIdServletFilter.class)
public class TydicWebConfiguration implements WebMvcConfigurer, RewriteEnvironmentAware {

    @Override
    public void setEnvironment(Environment environment) {
        this.setBlankProperty("#接口超时时间\n" +
                "spring.mvc.async.request-timeout=30S\n" +
                "\n" +
                "#文件上传大小限制\n" +
                "spring.servlet.multipart.max-file-size=1GB\n" +
                "\n" +
                "#请求大小限制\n" +
                "spring.servlet.multipart.max-request-size=1GB");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN));
        registrar.setDateFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        registrar.registerFormatters(registry);
        //Date
        registry.addConverter(TimeConverters.stringToDate);
        //DateTime
        registry.addConverter(TimeConverters.stringToDateTime);
        //LocalTime
        registry.addConverter(TimeConverters.stringToLocalTime);
        //LocalDate
        registry.addConverter(TimeConverters.stringToLocalDate);
        //LocalDateTime
        registry.addConverter(TimeConverters.stringToLocalDateTime);
        //枚举
        registry.addConverterFactory(StringToEnumConverterFactory.instance);
    }


    @Bean
    public UndertowDeploymentInfoCustomizer undertowDeploymentInfoCustomizer() {
        return deploymentInfo -> {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();
            deploymentInfo.setExceptionHandler(handler);
        };
    }


    @EventListener
    public void showTydicWebBanner(ApplicationReadyEvent event) {
        String doc = NetUtil.localIpv4s()
                .stream()
                .sorted(Comparator.comparingLong(NetUtil::ipv4ToLong))
                .map(it -> "http://" + it + ":" + SpringUtil.getServerPort() + "/doc.html")
                .collect(Collectors.joining("\n┃ 　　   "));
        String banner = StrUtil.format(("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n" +
                        "┃ 服务 : {}\n" +
                        "┃ 端口 : {}\n" +
                        "┃ 状态 : 启动成功\n" +
                        "┃ 时间 : {}\n" +
                        "┃ 耗时 : {}\n" +
                        "┃ 实例 : {}\n" +
                        "┃ 接口 : {}\n" +
                        "┃ 文档 : {}\n" +
                        "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛").trim()
                , SpringUtil.getApplicationName()
                , SpringUtil.getServerPort()
                , DateTime.now().toMsStr()
                , DateUtil.formatBetween(ManagementFactory.getRuntimeMXBean().getUptime())
                , SpringUtil.getApplicationContext().getBeanDefinitionCount()
                , this.requestMethodSize()
                , doc
        );
        System.out.println(banner);
    }

    private int requestMethodSize() {
        try {
            RequestMappingHandlerMapping handlerMapping = SpringUtil
                    .getBeansOfType(RequestMappingHandlerMapping.class)
                    .values()
                    .stream()
                    .filter(it -> it.getClass().equals(RequestMappingHandlerMapping.class))
                    .findAny()
                    .orElse(null);
            if (handlerMapping == null) {
                return -1;
            }
            return handlerMapping.getHandlerMethods().size();
        } catch (Exception e) {
            return -1;
        }
    }


}
