package easier.framework.test.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import easier.framework.test.eo.Menu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单表
 * t_menu
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Mapper
public interface MenuMapper extends MPJBaseMapper<Menu> {

}
