package easier.framework.test.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import easier.framework.test.eo.DictItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典项
 * t_dict_item
 *
 * @author lizhian
 * @date 2023年07月05日
 */
@Mapper
public interface DictItemMapper extends MPJBaseMapper<DictItem> {

}
