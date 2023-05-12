package tydic.framework.test.repository;

import org.springframework.stereotype.Component;
import tydic.framework.core.plugin.mybatis.EntityConfigurer;
import tydic.framework.starter.mybatis.repo.BaseRepo;
import tydic.framework.test.eo.SysDict;

@Component
public class SysDictRepo extends BaseRepo<SysDict> {


    public SysDictRepo() {
        super(this.baseMapper);
    }

    @Override
    protected void init(EntityConfigurer<SysDict> configurer) {
        configurer.codeColumn(SysDict::getDictCode);
    }
}
