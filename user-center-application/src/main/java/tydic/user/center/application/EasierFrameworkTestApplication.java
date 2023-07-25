package tydic.user.center.application;

import com.tangzc.mpe.autotable.EnableAutoTable;
import easier.framework.application.web.EasierWebApplication;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.mybatis.EnableEasierMybatis;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@EasierWebApplication
@EnableEasierMybatis
@MapperScan(annotationClass = Mapper.class)
@EnableAutoTable(activeProfile = SpringUtil.dev)
public class EasierFrameworkTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasierFrameworkTestApplication.class, args);
    }
}
