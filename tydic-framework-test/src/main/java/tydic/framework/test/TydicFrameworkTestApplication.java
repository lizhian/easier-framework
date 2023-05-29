package tydic.framework.test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tydic.framework.starter.discovery.EnableTydicDiscovery;
import tydic.framework.test.eo.SysApp;

@Slf4j
@MapperScan
@SpringBootApplication
@EnableTydicDiscovery
public class TydicFrameworkTestApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SysApp eo = new SysApp();

        /*for (int i = 1; i < 6000; i++) {
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

        }*/


        SpringApplication.run(TydicFrameworkTestApplication.class, args);
    }
}
