package tydic.framework.test.repository;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tydic.framework.test.eo.SysApp;
import tydic.framework.test.eo.SysDict;
import tydic.framework.test.eo.relation.AppToDict;

public interface Mappers {
    @Mapper
    interface SysAppMapper extends MPJBaseMapper<SysApp> {
    }

    @Mapper
    interface SysDictMapper extends MPJBaseMapper<SysDict> {

    }

    @Mapper
    interface AppToDictMapper extends MPJBaseMapper<AppToDict> {
    }


}
