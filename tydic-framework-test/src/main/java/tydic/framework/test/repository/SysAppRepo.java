package tydic.framework.test.repository;

import org.springframework.stereotype.Component;
import tydic.framework.starter.mybatis.base.BaseRepo;
import tydic.framework.starter.mybatis.base.entity.EntityConfigurer;
import tydic.framework.test.eo.SysApp;

@Component
public class SysAppRepo extends BaseRepo<SysApp> {


    @Override
    protected void init(EntityConfigurer<SysApp> configurer) {
        configurer.codeColumn(SysApp::getAppCode);
    }
}
