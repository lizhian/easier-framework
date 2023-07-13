package easier.framework.test.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.PageParam;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.eo.Role;
import easier.framework.test.qo.RoleAssignAppQo;
import easier.framework.test.qo.RolePageQo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 角色服务
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Service
@RequiredArgsConstructor
public class RoleService {
    private final Repo<Role> _sys_role = Repos.of(Role.class);

    public Page<Role> pageRole(PageParam pageParam, RolePageQo qo) {
        return this._sys_role.newQuery().page(pageParam.toPage());
    }

    public void addRole(Role entity) {
        this._sys_role.add(entity);
    }

    public void updateRole(Role entity) {
        this._sys_role.update(entity);
    }

    public void deleteRole(String code) {

    }

    public void assignApp(RoleAssignAppQo qo) {

    }
}
