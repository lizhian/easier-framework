package easier.framework.test.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import easier.framework.test.eo.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色表
 * t_user_role
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Mapper
public interface UserRoleMapper extends MPJBaseMapper<UserRole> {

}
