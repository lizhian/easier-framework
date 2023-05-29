package tydic.framework.test.service;

import org.springframework.stereotype.Component;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.mybatis.repo.EntityRepo;
import tydic.framework.starter.mybatis.repo.Repos;
import tydic.framework.test.SysAppMapper;
import tydic.framework.test.eo.AppToDict;
import tydic.framework.test.eo.SysApp;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Component
public class TestService {

    @Resource
    private SysAppMapper sysAppMapper;

    public void test() {
        List<SysApp> sysApps = sysAppMapper.myList();
        EntityRepo<SysApp> repo = Repos.of(SysApp.class);
        repo.newQuery()
                .whenNotBlank()
                .eq(SysApp::getAppAddr, "")
                .end()
                .list();

        Set<String> valueSet = Repos.of(AppToDict.class)
                .withPair(AppToDict::getAppCode, AppToDict::getDictCode)
                .delete();


    }
}
