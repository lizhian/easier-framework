package easier.framework.test.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import easier.framework.test.eo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * t_user
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Mapper
public interface UserMapper extends MPJBaseMapper<User> {

}
