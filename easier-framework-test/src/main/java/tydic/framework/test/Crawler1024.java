package easier.framework.test;

import cn.hutool.core.util.NumberUtil;
import easier.framework.core.util.StrUtil;
import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.stream.Collectors;

public class Crawler1024 {
    public static void main(String[] args) {
        for (int i = 6000; i < 10000; i++) {
            crawl(i);
        }
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
