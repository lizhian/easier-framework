package easier.framework.test.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import easier.framework.test.eo.Dept;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门表
 * t_dept
 *
 * @author lizhian
 * @date 2023/07/05
 */
@Mapper
public interface DeptMapper extends MPJBaseMapper<Dept> {

}
