package easier.framework.test.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import easier.framework.test.eo.ApplicationConfiguration;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用配置表
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Mapper
public interface ApplicationConfigurationMapper extends MPJBaseMapper<ApplicationConfiguration> {

}
