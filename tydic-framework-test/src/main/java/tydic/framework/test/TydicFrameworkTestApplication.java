package tydic.framework.test;

import cn.hutool.core.util.NumberUtil;
import com.tangzc.mpe.actable.EnableAutoTable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.mybatis.EnableTydicMybatis;
import tydic.framework.test.enums.EnableStatus;
import tydic.framework.test.enums.SysAppType;
import tydic.framework.test.enums.SysDictType;
import tydic.framework.test.eo.SysApp;
import tydic.framework.test.eo.SysDict;
import tydic.framework.test.eo.relation.AppToDict;
import tydic.framework.test.repository.AppToDictRepo;
import tydic.framework.test.repository.SysAppRepo;
import tydic.framework.test.repository.SysDictRepo;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@MapperScan
@EnableAutoTable
@SpringBootApplication
@EnableCaching
@EnableTydicMybatis
public class TydicFrameworkTestApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SysApp eo = new SysApp();

        for (int i = 1; i < 6000; i++) {
            String url = "https://www.1024zyz.com/" + i + ".html";
            Connection.Response response = null;
            try {
                response = Jsoup.connect(url).execute();
            } catch (IOException e) {
                //System.out .println("跳过:"+url);
                continue;
            }
            if (response.statusCode() != 200) {
                //System.out.println("跳过:"+url);
                continue;
            }
            Document document = response.parse();
            Element title = document.selectFirst(".entry-title");
            if (title == null) {
                //System.out.println("跳过:"+url);
                continue;
            }
            Element meta = document.selectFirst(".entry-meta");
            System.out.println(url + " -> [" + _String(meta) + "] -> " + title.ownText());

        }


        SpringApplication.run(TydicFrameworkTestApplication.class, args);
        SysAppRepo sysAppRepo = SpringUtil.getBean(SysAppRepo.class);
        SysDictRepo sysDictRepo = SpringUtil.getBean(SysDictRepo.class);
        AppToDictRepo appToDictRepo = SpringUtil.getBean(AppToDictRepo.class);
        //List<SysApp> all = sysAppRepo.all();
        for (int i = 0; i < 10; i++) {
            SysApp app = SysApp.builder()
                               .appCode(i + "")
                               .appName(i + "")
                               .enableStatus(EnableStatus.enable)
                               .appType(SysAppType.inside)
                               .build();
            sysAppRepo.add(app);
            SysDict dict = SysDict.builder()
                                  .dictCode(i + "")
                                  .dictName(i + "")
                                  .dictType(SysDictType.sys)
                                  .enableStatus(EnableStatus.enable)
                                  .build();
            sysDictRepo.add(dict);
            AppToDict appToDict = AppToDict.builder()
                                           .appCode(i + "")
                                           .dictCode(i + "")
                                           .build();
            //appToDictRepo.add(appToDict);
        }
        sysAppRepo.newUpdate()
                  .set(SysApp::getAppName, "222")
                  .update();
        var list = sysAppRepo.newQuery()
                             .bind()
                             .limit(10);
        var appCodes = list.stream()
                           .map(SysApp::getAppCode)
                           .toList();
        /*var list1 = sysAppRepo.newQuery()
                              .ifNotEmpty().in(SysApp::getAppCode, appCodes)
                              .list();
        boolean b = sysAppRepo.withRelateDelete()
                              .deleteByCodes(appCodes);
        log.info("");
        new Date().getTime();
        System.currentTimeMillis();*/


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
