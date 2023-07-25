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
import easier.framework.test.eo.App;
import easier.framework.test.eo.RoleApp;
import easier.framework.test.eo.RoleMenu;
import easier.framework.test.qo.AppAssignRoleQo;
import easier.framework.test.qo.AppQo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 应用服务
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Service
@RequiredArgsConstructor
@ExtensionMethod(ExtensionCore.class)
public class AppService {

    private final Repo<App> _app = Repos.of(App.class);
    private final Repo<RoleApp> _role_app = Repos.of(RoleApp.class);
    private final Repo<RoleMenu> _role_menu = Repos.of(RoleMenu.class);

    public List<App> list(AppQo qo) {
        return this._app.newQuery()
                .whenNotBlank()
                .like(App::getAppName, qo.getAppName())
                .like(App::getAppCode, qo.getAppCode())
                .whenNotNull()
                .eq(App::getStatus, qo.getStatus())
                .end()
                .orderByAsc(App::getSort)
                .list();
    }

    public Page<App> pageApp(PageParam pageParam, AppQo qo) {
        return this._app.newQuery()
                .whenNotBlank()
                .like(App::getAppName, qo.getAppName())
                .like(App::getAppCode, qo.getAppCode())
                .whenNotNull()
                .eq(App::getStatus, qo.getStatus())
                .end()
                .orderByAsc(App::getSort)
                .bind(App::getRoles, App::getRoleCodes)
                .page(pageParam.toPage());
    }

    public void addApp(App entity) {
        ValidUtil.valid(entity);
        String appCode = entity.getAppCode();
        String appName = entity.getAppName();
        this._app.newQuery()
                .eq(App::getAppCode, appCode)
                .existsThenThrow("重复的应用编码:{}", appCode);
        this._app.newQuery()
                .eq(App::getAppName, appName)
                .existsThenThrow("重复的应用名称:{}", appName);
        this._app.add(entity);
    }

    public void updateApp(App entity) {
        ValidUtil.validOnUpdate(entity);
        String appId = entity.getAppId();
        App old = this._app.getById(appId);
        if (old == null) {
            throw BizException.of("无效的应用主键:{}", appId);
        }
        String appName = entity.getAppName();
        this._app.newQuery()
                .eq(App::getAppName, appName)
                .ne(App::getAppId, appId)
                .existsThenThrow("重复的应用名称:{}", appName);
        entity.setAppCode(old.getAppCode());
        entity.copyBaseField(old);
        this._app.update(entity);
    }

    @Transactional
    public void deleteApp(String appCode) {
        App old = this._app.getByCode(appCode);
        if (old == null) {
            throw BizException.of("无效的应用编码:{}", appCode);
        }
        this._role_menu.newQuery()
                .eq(RoleMenu::getAppCole, appCode)
                .existsThenThrow("应用存在已授权的角色权限,无法删除");
        this._role_app.newQuery()
                .eq(RoleApp::getAppCole, appCode)
                .existsThenThrow("应用存在已授权的角色,无法删除");
        this._app.deleteByCode(appCode);
        this._role_app.deleteBy(RoleApp::getAppCole, appCode);
        this._role_menu.deleteBy(RoleMenu::getAppCole, appCode);
    }

    @Transactional
    public void assignRole(AppAssignRoleQo qo) {
        ValidUtil.valid(qo);
        String appCode = qo.getAppCode();
        this._role_app.deleteBy(RoleApp::getAppCole, appCode);
        List<RoleApp> roleApps = qo.getRoleCodes().orEmpty()
                .stream()
                .filter(StrUtil::isNotBlank)
                .map(roleCode -> RoleApp.builder()
                        .appCole(appCode)
                        .roleCode(roleCode)
                        .build())
                .collect(Collectors.toList());
        this._role_app.addBatch(roleApps);
    }


    public List<App> listByRoleCode(String roleCode) {
        List<String> appCodes = this._role_app.withPair(RoleApp::getAppCole, RoleApp::getAppCole)
                .toValueList(roleCode);
        if (CollUtil.isEmpty(appCodes)) {
            return new ArrayList<>();
        }
        return this._app.listByCodes(appCodes);
    }
}
