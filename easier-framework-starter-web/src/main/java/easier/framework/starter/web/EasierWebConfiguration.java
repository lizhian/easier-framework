package easier.framework.starter.web;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.text.StrPool;
import easier.framework.core.domain.R;
import easier.framework.core.domain.RCode;
import easier.framework.core.plugin.exception.handler.ErrorConfigurer;
import easier.framework.core.plugin.exception.handler.ErrorHandlerRegistry;
import easier.framework.core.plugin.validation.ValidErrorDetail;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.cache.EnableEasierCache;
import easier.framework.starter.job.EnableEasierJob;
import easier.framework.starter.web.converter.StringToEnumConverterFactory;
import easier.framework.starter.web.converter.TimeConverters;
import easier.framework.starter.web.error.EasierErrorController;
import easier.framework.starter.web.filter.TraceIdServletFilter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.management.ManagementFactory;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tgd
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableEasierCache
@EnableEasierJob
@Import(TraceIdServletFilter.class)
public class EasierWebConfiguration implements WebMvcConfigurer {
    private final Function<BindingResult, Object> bindingResultResponseBody = bindingResult -> {
        List<ValidErrorDetail> errorDetails = bindingResult
                .getAllErrors()
                .stream()
                .map(ValidErrorDetail::from)
                .collect(Collectors.toList());
        List<String> properties = errorDetails.stream()
                .map(ValidErrorDetail::getProperty)
                .distinct()
                .collect(Collectors.toList());
        List<String> batchMessage = errorDetails.stream()
                .map(ValidErrorDetail::getMergeMessage)
                .collect(Collectors.toList());
        String message = StrUtil.join(StrPool.COMMA, batchMessage);
        R<Object> failed = R.failed(message);
        log.error("参数校验异常,属性:{},错误信息:{}", properties, message);
        Map<String, Object> expandData = MapUtil
                .<String, Object>builder("batchMessage", batchMessage)
                .put("errorDetails", errorDetails)
                .build();
        failed.setExpandData(expandData);
        return failed;
    };

    @Override
    public void addFormatters(@NotNull FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN));
        registrar.setDateFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        registrar.registerFormatters(registry);
        // Date
        registry.addConverter(TimeConverters.stringToDate);
        // DateTime
        registry.addConverter(TimeConverters.stringToDateTime);
        // LocalTime
        registry.addConverter(TimeConverters.stringToLocalTime);
        // LocalDate
        registry.addConverter(TimeConverters.stringToLocalDate);
        // LocalDateTime
        registry.addConverter(TimeConverters.stringToLocalDateTime);
        // 枚举
        registry.addConverterFactory(StringToEnumConverterFactory.instance);
    }

    @Bean
    public ErrorHandlerRegistry errorHandlerRegistry(ObjectProvider<ErrorConfigurer> errorConfigurers) {
        List<ErrorConfigurer> configurers = errorConfigurers.orderedStream().collect(Collectors.toList());
        ErrorHandlerRegistry registry = new ErrorHandlerRegistry();
        configurers.forEach(configurer -> configurer.addErrorHandler(registry));
        return registry;
    }


    @Bean
    public EasierErrorController easierErrorController(ErrorAttributes errorAttributes,
                                                       ObjectProvider<ErrorViewResolver> errorViewResolvers,
                                                       ErrorHandlerRegistry errorHandlerRegistry) {
        List<ErrorViewResolver> viewResolvers = errorViewResolvers.orderedStream().collect(Collectors.toList());
        return new EasierErrorController(errorAttributes, viewResolvers, errorHandlerRegistry);
    }


    @Bean
    public ErrorConfigurer defaultErrorConfigurer() {
        return registry -> {
            registry.of(NullPointerException.class)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .responseBody(error -> {
                        log.error("系统发生空指针异常", error);
                        return R.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统发生空指针异常,请联系管理员");
                    });
            registry.of(OutOfMemoryError.class)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .responseBody(error -> {
                        log.error("系统发生内存溢出", error);
                        return R.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统发生内存溢出,请联系管理员");
                    });
            registry.of(HttpRequestMethodNotSupportedException.class)
                    .responseBody(error -> {
                        String method = error.getMethod();
                        return R.failed("不支持使用[" + method + "]方法请求此接口");
                    });
            // 参数校验异常
            registry.of(MethodArgumentNotValidException.class)
                    .responseBody(error -> this.bindingResultResponseBody.apply(error.getBindingResult()));
            registry.of(BindException.class)
                    .responseBody(error -> this.bindingResultResponseBody.apply(error.getBindingResult()));
            // 越权异常
            registry.of(NotPermissionException.class)
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .responseBody(error -> {
                        log.error("越权操作:{}", error.toString());
                        return R.failed(RCode.not_permission);
                    });
            // 越权异常
            registry.of(NotRoleException.class)
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .responseBody(error -> {
                        log.error("越权操作:{}", error.toString());
                        return R.failed(RCode.not_permission);
                    });
            // 越权异常
            registry.of(NotLoginException.class)
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .responseBody(error -> {
                        log.error("越权操作:{}", error.toString());
                        return R.failed(RCode.not_login);
                    });
            registry.of(SaOAuth2Exception.class)
                    .responseBody(error -> {
                        log.error(error.getMessage(), error);
                        return R.failed(error.getMessage());
                    });
        };
    }


    @Bean
    public UndertowDeploymentInfoCustomizer undertowDeploymentInfoCustomizer() {
        return deploymentInfo -> {
            deploymentInfo.setExceptionHandler((exchange, request, response, throwable) -> false);
        };
    }


    @EventListener(ApplicationReadyEvent.class)
    public void showEasierWebBanner() {
        ServerProperties serverProperties = SpringUtil.getBean(ServerProperties.class);
        String doc = NetUtil.localIpv4s()
                .stream()
                .sorted(Comparator.comparingLong(NetUtil::ipv4ToLong))
                .map(it -> {
                    StringBuilder builder = StrUtil.builder()
                            .append("http://")
                            .append(it)
                            .append(":")
                            .append(SpringUtil.getServerPort());
                    String contextPath = serverProperties.getServlet().getContextPath();
                    if (StrUtil.isBlank(contextPath)) {
                        return builder.append("/doc.html").toString();
                    }
                    if (!contextPath.startsWith("/")) {
                        builder.append("/");
                    }
                    builder.append(contextPath);
                    if (contextPath.endsWith("/")) {
                        return builder.append("doc.html").toString();
                    }
                    return builder.append("/doc.html").toString();
                })
                .collect(Collectors.joining("\n┃ 　　   "));
        String template = "\n" +
                "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n" +
                "┃ 服务 : {}\n" +
                "┃ 端口 : {}\n" +
                "┃ 状态 : 启动成功\n" +
                "┃ 时间 : {}\n" +
                "┃ 耗时 : {}\n" +
                "┃ 实例 : {}\n" +
                "┃ 接口 : {}\n" +
                "┃ 文档 : {}\n" +
                "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛";
        String banner = StrUtil.format(template.trim()
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
