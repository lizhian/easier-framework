package easier.framework.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import easier.framework.test.eo.RunningLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 运行日志表
 * t_running_log
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Mapper
public interface RunningLogMapper extends BaseMapper<RunningLog> {

}
