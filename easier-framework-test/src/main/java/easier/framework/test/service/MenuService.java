package easier.framework.test.service;

import cn.hutool.core.map.MapUtil;
import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.plugin.tree.TreeNode;
import easier.framework.core.util.*;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.enums.MenuType;
import easier.framework.test.eo.Menu;
import easier.framework.test.qo.MenuTreeQo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * 菜单服务
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Service
@RequiredArgsConstructor
@ExtensionMethod(ExtensionCore.class)
public class MenuService {
    private static final Map<String, String> BASE_PERMS = MapUtil
            .builder("query", "基础查询权限")
            .put("edit", "基础编辑权限")
            .put("delete", "基础删除权限")
            .build();

    private final Repo<Menu> _menu = Repos.of(Menu.class);


    public List<TreeNode<Menu>> tree(MenuTreeQo qo) {
        ValidUtil.valid(qo);
        List<Menu> list = this._menu.newQuery()
                .eq(Menu::getAppCode, qo.getAppCode())
                .whenNotNull()
                .eq(Menu::getMenuType, qo.getMenuType())
                .eq(Menu::getStatus, qo.getStatus())
                .end()
                .orderByAsc(Menu::getSort)
                .list();
        List<TreeNode<Menu>> tree = Menu.treeBuilder.build(list);
        String menuName = qo.getMenuName();
        String perms = qo.getPerms();
        if (StrUtil.isNotBlank(menuName)) {
            tree = Menu.treeBuilder.include(tree, menu -> menu.getMenuName().orEmpty().contains(menuName));
        }
        if (StrUtil.isNotBlank(perms)) {
            tree = Menu.treeBuilder.include(tree, menu -> menu.getPerms().orEmpty().contains(perms));
        }
        return tree;
    }

    @Transactional
    public void add(Menu entity) {
        ValidUtil.valid(entity);
        String parentId = entity.getParentId();
        String menuName = entity.getMenuName();
        String appCode = entity.getAppCode();
        Menu parent = null;
        if (StrUtil.isBlank(parentId)) {
            entity.setParentId(TreeUtil.ROOT_KEY);
            entity.setAncestors(TreeUtil.ROOT_KEY);
        } else {
            parent = this._menu.getById(parentId);
            if (parent == null) {
                throw BizException.of("无效的父菜单主键:{}", parentId);
            }
            entity.setAncestors(parent.getAncestors() + "," + parentId);
        }
        this._menu.newQuery()
                .eq(Menu::getParentId, parentId)
                .eq(Menu::getMenuName, menuName)
                .existsThenThrow("重复的名称:{}", menuName);
        //新增目录
        if (MenuType.isDir(entity.getMenuType())) {
            if (parent != null && !MenuType.isDir(parent.getMenuType())) {
                throw BizException.of("目录类型的上级菜单只能是目录");
            }
            entity.setIsCache(null);
            entity.setComponent(null);
            entity.setQuery(null);
            entity.setPerms(null);
            if (entity.getIsFrame() == null) {
                throw BizException.of("是否外链不能为空");
            }
            if (entity.getIsVisible() == null) {
                throw BizException.of("是否可显示不能为空");
            }
            if (entity.getPath().isBlank()) {
                throw BizException.of("路由地址不能为空");
            }
            this._menu.add(entity);
            return;
        }

        //新增按钮
        if (MenuType.isButton(entity.getMenuType())) {
            if (parent == null) {
                throw BizException.of("按钮类型的上级菜单不能为空");
            }
            if (!MenuType.isMenu(parent.getMenuType())) {
                throw BizException.of("按钮类型的上级菜单只能是菜单");
            }
            entity.setIsCache(null);
            entity.setIsVisible(null);
            entity.setIsFrame(null);
            entity.setIcon(null);
            entity.setPath(null);
            entity.setComponent(null);
            entity.setQuery(null);
            if (entity.getPerms().isBlank()) {
                throw BizException.of("按钮的权限标识不能为空");
            }
            if (entity.getPerms().startsWith(appCode + ":")) {
                throw BizException.of("权限标识必须以 {}: 作为前缀", appCode);
            }
            this._menu.newQuery()
                    .eq(Menu::getPerms, entity.getPerms())
                    .existsThenThrow("重复的权限标识 {}", entity.getPerms());
            this._menu.add(entity);
            return;
        }

        //新增菜单
        if (parent != null && !MenuType.isDir(parent.getMenuType())) {
            throw BizException.of("菜单类型的上级菜单只能是目录");
        }
        if (entity.getIsFrame() == null) {
            throw BizException.of("是否外链不能为空");
        }
        if (entity.getIsVisible() == null) {
            throw BizException.of("是否可显示不能为空");
        }
        if (entity.getIsCache() == null) {
            throw BizException.of("是否缓存不能为空");
        }
        if (entity.getPath().isBlank()) {
            throw BizException.of("路由地址不能为空");
        }
        if (entity.getComponent().isBlank()) {
            throw BizException.of("组件路径不能为空");
        }
        if (entity.getPerms().isBlank()) {
            throw BizException.of("权限标识不能为空");
        }
        if (!entity.getPerms().startsWith(appCode + ":")) {
            throw BizException.of("权限标识必须以 {}: 作为前缀", appCode);
        }
        if (entity.getPerms().equals(appCode + ":")) {
            throw BizException.of("请填写完整的权限标识");
        }
        this._menu.newQuery()
                .eq(Menu::getPerms, entity.getPerms())
                .existsThenThrow("重复的权限标识 {}", entity.getPerms());
        entity.setMenuId(IdUtil.nextIdStr());
        this._menu.add(entity);

        //给菜单添加基础权限
        AtomicInteger sort = new AtomicInteger();
        List<Menu> baseButtons = BASE_PERMS.entrySet()
                .stream()
                .map(entry -> Menu.builder()
                        .appCode(appCode)
                        .menuType(MenuType.button)
                        .menuName(entry.getValue())
                        .parentId(entity.getMenuId())
                        .ancestors(entity.getAncestors() + "," + entity.getMenuId())
                        .sort(sort.getAndIncrement())
                        .status(EnableStatus.enable)
                        .perms(entity.getPerms() + ":" + entry.getKey())
                        .build()
                )
                .collect(Collectors.toList());
        this._menu.addBatch(baseButtons);
    }

}
