package easier.framework.test.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import easier.framework.test.eo.Dict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典表
 * t_dict
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Mapper
public interface DictMapper extends MPJBaseMapper<Dict> {

}
