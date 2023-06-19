package tydic.framework.test;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.discovery.EnableTydicDiscovery;
import tydic.framework.starter.env.JasyptUtil;
import tydic.framework.starter.job.EnableTydicJob;
import tydic.framework.starter.job.center.JobController;
import tydic.framework.starter.logging.EnableTydicLogging;
import tydic.framework.starter.mybatis.EnableTydicMybatis;
import tydic.framework.starter.web.EnableTydicWeb;
import tydic.framework.test.service.JobTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@MapperScan
@SpringBootApplication
@EnableTydicDiscovery
@EnableTydicMybatis
@EnableTydicJob
@EnableTydicWeb
@EnableTydicLogging
public class TydicFrameworkTestApplication {

    @SneakyThrows
    public static void main(String[] args) {

        JasyptUtil.encryptConsole("123456", "123456");
        JasyptUtil.decryptConsole("123456", "ENC(ONnJgQKgRSdvqGN3iprXxq8gC49Eq9DHARiKtNbsu3J+/Wcc6j+3cc3cYctPuiSq)");

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
        AES aes = SecureUtil.aes("asdasd".getBytes(StandardCharsets.UTF_8));

        SpringApplication.run(TydicFrameworkTestApplication.class, args);
        StringEncryptor encryptor = SpringUtil.getBean(StringEncryptor.class);
        String encrypt = encryptor.encrypt("123456");
        JobController jobController = SpringUtil.getBean(JobController.class);
        JobTest jobTest = new JobTest();
        jobController.submit(jobTest, JobTest::test);
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
