package easier.framework.test.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import easier.framework.test.eo.App;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用表
 * t_app
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Mapper
public interface AppMapper extends MPJBaseMapper<App> {

}
