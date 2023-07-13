package easier.framework.test.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import easier.framework.test.eo.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色
 * t_role
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Mapper
public interface RoleMapper extends MPJBaseMapper<Role> {

}
