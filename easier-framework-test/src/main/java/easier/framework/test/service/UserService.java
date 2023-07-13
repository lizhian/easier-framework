package easier.framework.test.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import easier.framework.core.domain.PageParam;
import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.util.ExtensionCore;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.ValidUtil;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.eo.User;
import easier.framework.test.eo.UserRole;
import easier.framework.test.qo.ResetPasswordQo;
import easier.framework.test.qo.UserAssignRoleQo;
import easier.framework.test.qo.UserQo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 用户服务
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Service
@RequiredArgsConstructor
@ExtensionMethod(ExtensionCore.class)
public class UserService {
    private final Repo<User> _user = Repos.of(User.class);
    private final Repo<UserRole> _user_role = Repos.of(UserRole.class);
    private final StringEncryptor encryptor;


    public Page<User> pageUser(PageParam pageParam, UserQo qo) {
        List<String> deptIds = new ArrayList<>();
        if (StrUtil.isNotBlank(qo.getDeptId())) {

        }
        return this._user.newQuery()
                .whenNotBlank()
                .like(User::getUsername, qo.getUsername())
                .like(User::getNickname, qo.getNickname())
                .like(User::getPhone, qo.getPhone())
                .whenNotNull()
                .eq(User::getStatus, qo.getStatus())
                .whenNotEmpty()
                .in(User::getDeptId, deptIds)
                .end()
                .page(pageParam.toPage());
    }

    public void addUser(User entity) {
        ValidUtil.valid(entity);
        String username = entity.getUsername();
        this._user.newQuery()
                .eq(User::getUsername, username)
                .existsThenThrow("重复的用户账号:{}", username);
        String password = entity.getPassword();
        if (StrUtil.isBlank(password)) {
            throw BizException.of("密码不能为空");
        }
        entity.setPassword(this.encryptor.encrypt(password));
        this._user.add(entity);
    }

    public void updateUser(User entity) {
        ValidUtil.validOnUpdate(entity);
        String userId = entity.getUserId();
        User old = this._user.getById(userId);
        if (old == null) {
            throw BizException.of("无效的用户主键:{}", userId);
        }
        entity.copyBaseField(old);
        entity.setPassword(old.getPassword());
        entity.setLastLoginIp(old.getLastLoginIp());
        entity.setLastLoginTime(old.getLastLoginTime());
        this._user.update(entity);
    }

    public void resetPassword(ResetPasswordQo qo) {
        ValidUtil.valid(qo);
        String username = qo.getUsername();
        User user = this._user.getByCode(username);
        if (user == null) {
            throw BizException.of("无效的用户账号:{}", username);
        }
        String newPassword = this.encryptor.encrypt(qo.getNewPassword());
        this._user.newUpdate()
                .set(User::getPassword, newPassword)
                .updateByCode(username);
    }

    @Transactional
    public void deleteUser(String username) {
        User user = this._user.getByCode(username);
        if (user == null) {
            throw BizException.of("无效的用户账号:{}", username);
        }

        this._user.deleteByCode(username);
        this._user_role.deleteBy(UserRole::getUsername, username);
    }

    public void assignRole(UserAssignRoleQo qo) {
        ValidUtil.valid(qo);
        String username = qo.getUsername();
        this._user_role.deleteBy(UserRole::getUsername, username);
        List<UserRole> userRoles = qo.getRoleCodes()
                .orEmpty()
                .stream()
                .filter(StrUtil::isNotBlank)
                .map(roleCode -> UserRole.builder()
                        .username(username)
                        .roleCode(roleCode)
                        .build()
                )
                .collect(Collectors.toList());
        this._user_role.addBatch(userRoles);
    }
}
