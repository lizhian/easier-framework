package easier.framework.test.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.PageParam;
import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.util.ExtensionCore;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.ValidUtil;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.eo.*;
import easier.framework.test.qo.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Service
@RequiredArgsConstructor
@ExtensionMethod(ExtensionCore.class)
public class RoleService {
    private final Repo<Role> _role = Repos.of(Role.class);
    private final Repo<User> _user = Repos.of(User.class);
    private final Repo<Menu> _menu = Repos.of(Menu.class);
    private final Repo<UserRole> _user_role = Repos.of(UserRole.class);
    private final Repo<RoleApp> _role_app = Repos.of(RoleApp.class);
    private final Repo<RoleMenu> _role_menu = Repos.of(RoleMenu.class);

    public Page<Role> page(PageParam pageParam, RoleQo qo) {
        return this._role
                .newQuery()
                .whenNotBlank()
                .like(Role::getRoleCode, qo.getRoleCode())
                .like(Role::getRoleName, qo.getRoleName())
                .eq(Role::getRoleType, qo.getRoleType())
                .end()
                .bind(Role::getAppCodes, Role::getApps)
                .page(pageParam.toPage());
    }

    public List<Role> list(RoleQo qo) {
        return this._role
                .newQuery()
                .whenNotBlank()
                .like(Role::getRoleCode, qo.getRoleCode())
                .like(Role::getRoleName, qo.getRoleName())
                .like(Role::getRoleType, qo.getRoleType())
                .end()
                .list();
    }

    public void addRole(Role entity) {
        ValidUtil.valid(entity);
        String roleCode = entity.getRoleCode();
        String roleName = entity.getRoleName();
        this._role.newQuery()
                .eq(Role::getRoleCode, roleCode)
                .existsThenThrow("重复的角色编码:{}", roleCode);
        this._role.newQuery()
                .eq(Role::getRoleName, roleName)
                .existsThenThrow("重复的角色名称:{}", roleName);
        this._role.add(entity);
    }

    public void updateRole(Role entity) {
        ValidUtil.validOnUpdate(entity);
        String roleId = entity.getRoleId();
        Role old = this._role.getById(roleId);
        if (old == null) {
            throw BizException.of("无效的角色主键:{}", roleId);
        }
        String roleName = entity.getRoleName();
        this._role.newQuery()
                .eq(Role::getRoleName, roleName)
                .ne(Role::getRoleId, roleId)
                .existsThenThrow("重复的角色名称:{}", roleName);
        entity.setRoleCode(old.getRoleCode());
        entity.copyBaseField(old);
        this._role.update(entity);
    }

    @Transactional
    public void deleteRole(String code) {
        this._role_app.newQuery()
                .eq(RoleApp::getRoleCode, code)
                .existsThenThrow("角色已分配应用,无法删除");
        this._role.deleteByCode(code);
        this._role_app.deleteBy(RoleApp::getRoleCode, code);
        this._user_role.deleteBy(UserRole::getRoleCode, code);
        this._role_menu.deleteBy(RoleMenu::getRoleCode, code);
    }

    @Transactional
    public void assignApp(RoleAssignAppQo qo) {
        ValidUtil.valid(qo);
        String roleCode = qo.getRoleCode();
        List<RoleApp> roleApps = qo.getAppCodes()
                .orEmpty()
                .stream()
                .filter(StrUtil::isNotBlank)
                .distinct()
                .map(appCode -> RoleApp.builder()
                        .appCole(appCode)
                        .roleCode(roleCode)
                        .build()
                )
                .collect(Collectors.toList());
        this._role_app.deleteBy(RoleApp::getRoleCode, roleCode);
        this._role_app.addBatch(roleApps);
    }


    public Page<User> assignedUserPage(String roleCode, UserQo qo, PageParam pageParam) {
        if (StrUtil.isBlank(roleCode)) {
            throw BizException.of("角色编码不能为空");
        }
        String username = qo.getUsername();
        String nickname = qo.getNickname();
        EnableStatus status = qo.getStatus();
        return this._user.newJoinQuery()
                .selectAll(User.class)
                .leftJoin(UserRole.class, on -> on
                        .eq(User::getUsername, UserRole::getUsername)
                        .eq(UserRole::getRoleCode, roleCode)
                )
                .isNotNull(UserRole::getRoleCode)
                .like(StrUtil.isNotBlank(username), User::getUsername, username)
                .like(StrUtil.isNotBlank(nickname), User::getNickname, nickname)
                .eq(status != null, User::getStatus, status)
                .page(pageParam.toPage(), User.class);
    }

    public Page<User> unassignedUserPage(String roleCode, UserQo qo, PageParam pageParam) {
        if (StrUtil.isBlank(roleCode)) {
            throw BizException.of("角色编码不能为空");
        }
        String username = qo.getUsername();
        String nickname = qo.getNickname();
        EnableStatus status = qo.getStatus();
        return this._user.newJoinQuery()
                .selectAll(User.class)
                .leftJoin(UserRole.class, on -> on
                        .eq(User::getUsername, UserRole::getUsername)
                        .eq(UserRole::getRoleCode, roleCode)
                )
                .isNull(UserRole::getRoleCode)
                .like(StrUtil.isNotBlank(username), User::getUsername, username)
                .like(StrUtil.isNotBlank(nickname), User::getNickname, nickname)
                .eq(status != null, User::getStatus, status)
                .page(pageParam.toPage(), User.class);
    }

    @Transactional
    public void assignUser(RoleAssignUserQo qo) {
        ValidUtil.valid(qo);
        String roleCode = qo.getRoleCode();
        List<String> usernames = qo.getUsernames();
        if (!this._role.existsByCode(roleCode)) {
            throw BizException.of("非法的角色编码:{}", roleCode);
        }
        if (this._user.countByCodes(usernames) < usernames.size()) {
            throw BizException.of("非法的用户账号:{}", usernames);
        }
        this._user_role.newUpdate()
                .eq(UserRole::getRoleCode, roleCode)
                .in(UserRole::getUsername, usernames)
                .remove();
        List<UserRole> list = qo.getUsernames()
                .stream()
                .map(username -> UserRole.builder()
                        .username(username)
                        .roleCode(roleCode)
                        .build()
                )
                .collect(Collectors.toList());
        this._user_role.addBatch(list);

    }

    public void unAssignUser(RoleAssignUserQo qo) {
        ValidUtil.valid(qo);
        String roleCode = qo.getRoleCode();
        List<String> usernames = qo.getUsernames();
        if (!this._role.existsByCode(roleCode)) {
            throw BizException.of("非法的角色编码:{}", roleCode);
        }
        this._user_role.newUpdate()
                .eq(UserRole::getRoleCode, roleCode)
                .in(UserRole::getUsername, usernames)
                .remove();
    }

    public List<String> menuSelected(RoleMenuQo qo) {
        ValidUtil.valid(qo);
        return this._role_menu.newQuery()
                .eq(RoleMenu::getAppCole, qo.getAppCode())
                .eq(RoleMenu::getRoleCode, qo.getRoleCode())
                .list()
                .stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignMenu(RoleMenuQo qo) {
        ValidUtil.valid(qo);
        this._role_menu.newUpdate()
                .eq(RoleMenu::getAppCole, qo.getAppCode())
                .eq(RoleMenu::getRoleCode, qo.getRoleCode())
                .remove();
        List<Menu> menus = this._menu.listByIds(qo.getMenuIds());
        if (CollUtil.isEmpty(menus)) {
            return;
        }
        List<RoleMenu> list = menus.stream()
                .map(menu -> RoleMenu.builder()
                        .menuId(menu.getMenuId())
                        .appCole(menu.getAppCode())
                        .roleCode(qo.getRoleCode())
                        .build()
                )
                .collect(Collectors.toList());
        this._role_menu.addBatch(list);
    }
}
