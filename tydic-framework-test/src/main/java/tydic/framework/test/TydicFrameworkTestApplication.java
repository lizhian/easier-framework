package tydic.framework.test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.github.yulichang.base.MPJBaseMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tydic.framework.starter.discovery.EnableTydicDiscovery;
import tydic.framework.starter.mybatis.EnableTydicMybatis;
import tydic.framework.starter.mybatis.repo.Repo;
import tydic.framework.starter.mybatis.repo.Repos;
import tydic.framework.test.eo.SysApp;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@MapperScan
@SpringBootApplication
@EnableTydicDiscovery
@EnableTydicMybatis
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
        Repo<SysApp> repo = Repos.of(SysApp.class);
        repo.addBatch(CollUtil.newArrayList(new SysApp()));
        MPJBaseMapper<SysApp> baseMapper = repo.getBaseMapper();
        Class<?> aClass = AopProxyUtils.ultimateTargetClass(baseMapper);
        Class<?>[] classes = AopProxyUtils.proxiedUserInterfaces(baseMapper);
        Class<?> aClass1 = Arrays.stream(classes)
                .filter(MPJBaseMapper.class::isAssignableFrom)
                .findAny().orElse(null);

        SysApp app = Repos.of(SysApp.class).getByCode("");
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
