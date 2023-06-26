package tydic.framework.test;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.HandlerMethod;
import tydic.framework.core.plugin.enums.EnumCodec;
import tydic.framework.core.plugin.enums.EnumDetail;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.auth.EnableTydicAuth;
import tydic.framework.starter.discovery.EnableTydicDiscovery;
import tydic.framework.starter.job.EnableTydicJob;
import tydic.framework.starter.logging.EnableTydicLogging;
import tydic.framework.starter.mybatis.EnableTydicMybatis;
import tydic.framework.starter.web.EnableTydicWeb;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@MapperScan
@SpringBootApplication
@EnableTydicDiscovery
@EnableTydicMybatis
@EnableTydicJob
@EnableTydicWeb
@EnableTydicLogging
@EnableTydicAuth
public class TydicFrameworkTestApplication {

    @SneakyThrows
    public static void main(String[] args) {


/*
        ThreadPoolExecutor executor = ExecutorBuilder.create()
                .setCorePoolSize(1)
                .build();


        for (int i = 1; i < 6000; i++) {
            //executor.submit(()->crawl(finalI));
            crawl(i);

        }
        while (executor.getActiveCount()>0){
            ThreadUtil.safeSleep(1000);
        }*/


        SpringApplication.run(TydicFrameworkTestApplication.class, args);

    }

    @Bean
    public PropertyCustomizer myPropertyCustomizer() {
        return (Schema property, AnnotatedType annotatedType) -> {
            forDescription(property, annotatedType);
            if (annotatedType.getType() instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) annotatedType.getType();
                if (simpleType.isEnumType()) {
                    Class<?> enumClazz = simpleType.getRawClass();
                    EnumCodec<?> enumCodec = EnumCodec.of(enumClazz);
                    List<String> values = enumCodec.getEnumDetails()
                            .stream()
                            .map(EnumDetail::getValueAsStr)
                            .collect(Collectors.toList());
                    property.setEnum(values);
                }
            }
            log.info("已处理 {} {}", annotatedType.getName(), annotatedType.getPropertyName());
            return property;
        };
    }

    private void forDescription(Schema property, AnnotatedType annotatedType) {
        if (StrUtil.isNotBlank(property.getDescription())) {
            return;
        }
        Annotation[] annotations = annotatedType.getCtxAnnotations();
        if (annotations == null) {
            return;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof Column) {
                Column column = (Column) annotation;
                if (StrUtil.isNotBlank(column.comment())) {
                    property.description(column.comment());
                    return;
                }
            }
            if (annotation instanceof Table) {
                Table table = (Table) annotation;
                if (StrUtil.isNotBlank(table.comment())) {
                    property.description(table.comment());
                    return;
                }
            }
        }
    }

    @Bean
    public OperationCustomizer myOperationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {

            return operation;
        };
    }

    @SneakyThrows
    private static void crawl(int i) {
        String url = "https://www.1024zyz.com/" + i + ".html";
        Connection.Response response = null;
        try {
            response = Jsoup.connect(url).execute();
        } catch (IOException e) {
            //System.out .println("跳过:"+url);
            return;
        }
        if (response.statusCode() != 200) {
            //System.out.println("跳过:"+url);
            return;
        }
        Document document = response.parse();
        Element title = document.selectFirst(".entry-title");
        if (title == null) {
            //System.out.println("跳过:"+url);
            return;
        }
        Element meta = document.selectFirst(".entry-meta");
        System.out.println(url + " -> [" + _String(meta) + "] -> " + title.ownText());

    }

    private static String _String(Element meta) {
        if (meta == null) {
            return "";
        }
        return meta.children().stream()
                .map(Element::text)
                .filter(s -> !NumberUtil.isNumber(s))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining(","));
    }
}
