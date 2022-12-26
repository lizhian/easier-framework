package tydic.framework.test.repository;

import org.springframework.stereotype.Component;
import tydic.framework.starter.mybatis.base.BaseRepo;
import tydic.framework.starter.mybatis.base.entity.EntityConfigurer;
import tydic.framework.test.eo.SysDict;

@Component
public class SysDictRepo extends BaseRepo<SysDict> {


    @Override
    protected void init(EntityConfigurer<SysDict> configurer) {
        configurer.codeColumn(SysDict::getDictCode);
    }
}
