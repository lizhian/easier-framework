package tydic.framework.test;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tydic.framework.test.eo.SysApp;

import java.util.List;

@Mapper
public interface SysAppMapper extends MPJBaseMapper<SysApp> {

    List<SysApp> myList();
}
