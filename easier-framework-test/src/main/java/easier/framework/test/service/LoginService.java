package easier.framework.test.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import easier.framework.core.plugin.auth.AuthContext;
import easier.framework.core.plugin.auth.detail.BaseAuthDetail;
import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.util.ExtensionCore;
import easier.framework.core.util.IdUtil;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.ValidUtil;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.cache.UserCenterCaches;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.enums.MenuType;
import easier.framework.test.eo.*;
import easier.framework.test.qo.PasswordLoginQo;
import easier.framework.test.vo.CaptchaDetail;
import easier.framework.test.vo.LoginUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录服务
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Service
@RequiredArgsConstructor
@ExtensionMethod(ExtensionCore.class)
public class LoginService {
    private final MathGenerator generator = new MathGenerator(1);

    private final Repo<User> _user = Repos.of(User.class);
    private final Repo<RoleApp> _role_app = Repos.of(RoleApp.class);
    private final Repo<RoleMenu> _role_menu = Repos.of(RoleMenu.class);
    private final Repo<Menu> _menu = Repos.of(Menu.class);
    private final StringEncryptor encryptor;


    public CaptchaDetail captcha(String oldCaptchaId) {
        if (StrUtil.isNotBlank(oldCaptchaId)) {
            UserCenterCaches.captcha.clean(oldCaptchaId);
        }
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(80, 32, 1, 32);
        captcha.setGenerator(this.generator);
        String captchaId = IdUtil.nextIdStr();
        String code = captcha.getCode();
        UserCenterCaches.captcha.update(captchaId, code);
        return CaptchaDetail.builder()
                .captchaId(captchaId)
                .imageBase64Data(captcha.getImageBase64Data())
                .build();
    }

    public LoginUserDetail login(PasswordLoginQo qo) {
        ValidUtil.valid(qo);
        String username = qo.getUsername();
        String password = qo.getPassword();
        String captchaId = qo.getCaptchaId();
        String code = qo.getCode();
        String srcCode = UserCenterCaches.captcha.get(captchaId);
        if (StrUtil.isBlank(srcCode)) {
            throw BizException.of("验证码已过期");
        }
        UserCenterCaches.captcha.clean(captchaId);
        if (!generator.verify(srcCode, code)) {
            throw BizException.of("验证码错误");
        }
        User user = this._user
                .withBind(User::getRoles, User::getDept)
                .getByCode(username);
        if (user == null) {
            throw BizException.of("账号或密码错误");
        }
        if (EnableStatus.isDisable(user.getStatus())) {
            throw BizException.of("账号已被禁用,请联系管理员");
        }
        if (!this.encryptor.decrypt(user.getPassword()).equals(password)) {
            throw BizException.of("账号或密码错误");
        }
        //查询关联的角色
        List<String> roleCodes = user.getRoles()
                .orEmpty()
                .stream()
                .filter(role -> EnableStatus.isEnable(role.getStatus()))
                .map(Role::getRoleCode)
                .distinct()
                .collect(Collectors.toList());
        //查询关联的菜单数据
        List<String> menuIds = this._role_menu
                .withPair(RoleMenu::getAppCole, RoleMenu::getMenuId)
                .toValueList(roleCodes);
        //查询关联的权限
        List<String> perms = this._menu.listByIds(menuIds)
                .stream()
                .filter(menu -> EnableStatus.isEnable(menu.getStatus()))
                .filter(menu -> MenuType.isButton(menu.getMenuType()))
                .map(Menu::getPerms)
                .distinct()
                .collect(Collectors.toList());
        AuthContext.login(username);
        BaseAuthDetail baseAuthDetail = BaseAuthDetail.builder()
                .roles(roleCodes)
                .permissions(perms)
                .build();
        AuthContext.setDetail(baseAuthDetail);
        String tokenValue = AuthContext.getTokenValue();
        String tokenName = StpUtil.stpLogic.getConfig().getTokenName();
        String tokenPrefix = StpUtil.stpLogic.getConfig().getTokenPrefix();
        return LoginUserDetail.builder()
                .tokenValue(tokenValue)
                .tokenName(tokenName)
                .tokenPrefix(tokenPrefix)
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getUserId())
                .deptId(user.getDeptId())
                .deptName(user.getDept() == null ? null : user.getDept().getDeptName())
                .roleCodes(roleCodes)
                .perms(perms)
                .build();
    }


}
