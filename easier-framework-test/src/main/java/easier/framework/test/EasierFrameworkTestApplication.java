package easier.framework.test;

import com.tangzc.mpe.autotable.EnableAutoTable;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.auth.EnableEasierAuth;
import easier.framework.starter.discovery.EnableEasierDiscovery;
import easier.framework.starter.doc.EnableEasierDoc;
import easier.framework.starter.job.EnableEasierJob;
import easier.framework.starter.logging.EnableEasierLogging;
import easier.framework.starter.mybatis.EnableEasierMybatis;
import easier.framework.starter.web.EnableEasierWeb;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@MapperScan
@SpringBootApplication
@EnableEasierDiscovery
@EnableEasierMybatis
@EnableEasierJob
@EnableEasierWeb
@EnableEasierLogging
@EnableEasierAuth
@EnableEasierDoc
@EnableAutoTable(activeProfile = SpringUtil.dev)
public class EasierFrameworkTestApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(EasierFrameworkTestApplication.class, args);
    }


}
