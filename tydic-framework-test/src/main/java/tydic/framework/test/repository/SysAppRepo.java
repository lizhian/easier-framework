package tydic.framework.test.repository;

import org.springframework.stereotype.Component;
import tydic.framework.core.plugin.mybatis.EntityConfigurer;
import tydic.framework.starter.mybatis.repo.BaseRepo;
import tydic.framework.test.eo.SysApp;

@Component
public class SysAppRepo extends BaseRepo<SysApp> {


    public SysAppRepo() {
        super(this.baseMapper);
    }

    @Override
    protected void init(EntityConfigurer<SysApp> configurer) {
        configurer.codeColumn(SysApp::getAppCode);
    }
}
