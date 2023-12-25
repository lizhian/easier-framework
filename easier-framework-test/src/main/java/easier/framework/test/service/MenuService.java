package easier.framework.test.service;

import cn.hutool.core.map.MapUtil;
import easier.framework.core.Easier;
import easier.framework.core.plugin.exception.biz.BizException;
import easier.framework.core.plugin.tree.TreeNode;
import easier.framework.core.util.ExtensionCore;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.TreeUtil;
import easier.framework.core.util.ValidUtil;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.enums.MenuType;
import easier.framework.test.eo.Menu;
import easier.framework.test.eo.RoleMenu;
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
    private final Repo<RoleMenu> _role_menu = Repos.of(RoleMenu.class);


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
        // 新增目录
        if (MenuType.isDir(entity.getMenuType())) {
            this.addDir(entity);
            return;
        }
        // 新增菜单
        if (MenuType.isMenu(entity.getMenuType())) {
            this.addMenu(entity);
            return;
        }
        // 新增按钮
        if (MenuType.isButton(entity.getMenuType())) {
            this.addButton(entity);
        }
    }

    /**
     * 添加目录
     *
     * @param entity 实体
     */
    private void addDir(Menu entity) {
        entity.setComponent(null);
        entity.setQuery(null);
        entity.setPerms(null);
        entity.setIsCache(null);
        ValidUtil.valid(entity, Menu.DirGroup.class);
        Menu parent = this._menu.getById(entity.getParentId());
        if (parent == null) {
            entity.setParentId(TreeUtil.ROOT_KEY);
            entity.setAncestors(TreeUtil.ROOT_KEY + ",");
        }
        if (parent != null) {
            if (!MenuType.isDir(parent.getMenuType())) {
                throw BizException.of("目录类型的上级只能是目录");
            }
            entity.setAncestors(parent.getAncestors() + entity.getParentId() + ",");
        }
        this._menu.newQuery()
                .eq(Menu::getAppCode, entity.getAppCode())
                .eq(Menu::getParentId, entity.getParentId())
                .eq(Menu::getMenuName, entity.getMenuName())
                .existsThenThrow("重复的目录名称:{}", entity.getMenuName());
        this._menu.add(entity);
    }

    /**
     * 添加菜单
     *
     * @param entity 实体
     */
    private void addMenu(Menu entity) {
        ValidUtil.valid(entity, Menu.MenuGroup.class);
        Menu parent = this._menu.getById(entity.getParentId());
        if (parent == null) {
            entity.setParentId(TreeUtil.ROOT_KEY);
            entity.setAncestors(TreeUtil.ROOT_KEY + ",");
        }
        if (parent != null) {
            if (!MenuType.isDir(parent.getMenuType())) {
                throw BizException.of("菜单类型的上级只能是目录");
            }
            entity.setAncestors(parent.getAncestors() + entity.getParentId() + ",");
        }
        String appCode = entity.getAppCode();
        this._menu.newQuery()
                .eq(Menu::getAppCode, appCode)
                .eq(Menu::getParentId, entity.getParentId())
                .eq(Menu::getMenuName, entity.getMenuName())
                .existsThenThrow("重复的菜单名称:{}", entity.getMenuName());

        if (!entity.getPerms().startsWith(appCode + ":")) {
            throw BizException.of("权限标识必须以 {}: 作为前缀", appCode);
        }
        if (entity.getPerms().equals(appCode + ":")) {
            throw BizException.of("请填写完整的权限标识");
        }
        this._menu.newQuery()
                .eq(Menu::getPerms, entity.getPerms())
                .existsThenThrow("重复的权限标识 {}", entity.getPerms());
        entity.setMenuId(Easier.Id.nextIdStr());
        this._menu.add(entity);

        // 给菜单添加基础权限
        AtomicInteger sort = new AtomicInteger();
        List<Menu> baseButtons = BASE_PERMS.entrySet()
                .stream()
                .map(entry -> Menu.builder()
                        .appCode(appCode)
                        .menuType(MenuType.button)
                        .menuName(entry.getValue())
                        .parentId(entity.getMenuId())
                        .ancestors(entity.getAncestors() + entity.getMenuId() + ",")
                        .sort(sort.getAndIncrement())
                        .status(EnableStatus.enable)
                        .perms(entity.getPerms() + ":" + entry.getKey())
                        .build()
                )
                .collect(Collectors.toList());
        this._menu.addBatch(baseButtons);
    }

    /**
     * 添加按钮
     *
     * @param entity 实体
     */
    private void addButton(Menu entity) {
        entity.setPath(null);
        entity.setComponent(null);
        entity.setIsCache(null);
        entity.setIsVisible(null);
        entity.setIsFrame(null);
        entity.setIcon(null);
        entity.setQuery(null);
        ValidUtil.valid(entity, Menu.ButtonGroup.class);
        String parentId = entity.getParentId();
        String appCode = entity.getAppCode();
        Menu parent = this._menu.getById(parentId);
        if (parent == null) {
            throw BizException.of("按钮类型的上级不能为空");
        }
        if (!MenuType.isDir(parent.getMenuType())) {
            throw BizException.of("按钮类型的上级只能是菜单");
        }
        entity.setAncestors(parent.getAncestors() + parentId + ",");
        if (entity.getPerms().startsWith(appCode + ":")) {
            throw BizException.of("权限标识必须以 {}: 作为前缀", appCode);
        }
        this._menu.newQuery()
                .eq(Menu::getPerms, entity.getPerms())
                .existsThenThrow("重复的权限标识 {}", entity.getPerms());
        this._menu.add(entity);
    }

    public void update(Menu entity) {
        ValidUtil.validOnUpdate(entity);
        String menuId = entity.getMenuId();
        Menu old = this._menu.getById(menuId);
        if (old == null) {
            throw BizException.of("无效的菜单主键:{}", menuId);
        }
        if (MenuType.isDir(old.getMenuType())) {
            this.updateDir(old, entity);
            return;
        }
        if (MenuType.isMenu(old.getMenuType())) {
            this.updateMenu(old, entity);
            return;
        }
        if (MenuType.isButton(old.getMenuType())) {
            this.updateButton(old, entity);
        }
    }

    /**
     * 更新目录
     *
     * @param old    老
     * @param entity 实体
     */
    private void updateDir(Menu old, Menu entity) {
        entity.copyBaseField(old);
        entity.setMenuId(old.getMenuId());
        entity.setAppCode(old.getAppCode());
        entity.setMenuType(old.getMenuType());
        entity.setIsCache(null);
        entity.setComponent(null);
        entity.setQuery(null);
        entity.setPerms(null);
        ValidUtil.valid(entity, Menu.DirGroup.class);
        Menu parent = this._menu.getById(entity.getParentId());
        if (parent == null) {
            entity.setParentId(TreeUtil.ROOT_KEY);
            entity.setAncestors(TreeUtil.ROOT_KEY + ",");
        }
        if (parent != null) {
            if (!MenuType.isDir(parent.getMenuType())) {
                throw BizException.of("目录类型的上级只能是目录");
            }
            entity.setAncestors(parent.getAncestors() + entity.getParentId() + ",");
        }
        this._menu.newQuery()
                .eq(Menu::getAppCode, entity.getAppCode())
                .eq(Menu::getParentId, entity.getParentId())
                .eq(Menu::getMenuName, entity.getMenuName())
                .ne(Menu::getMenuId, entity.getMenuId())
                .existsThenThrow("重复的名称:{}", entity.getMenuName());
        this._menu.update(entity);
        this.updateDescendants(entity);
    }

    /**
     * 更新菜单
     *
     * @param old    老
     * @param entity 实体
     */
    private void updateMenu(Menu old, Menu entity) {
        entity.copyBaseField(old);
        entity.setMenuId(old.getMenuId());
        entity.setAppCode(old.getAppCode());
        entity.setMenuType(old.getMenuType());
        entity.setPerms(old.getPerms());
        ValidUtil.valid(entity, Menu.MenuGroup.class);
        Menu parent = this._menu.getById(entity.getParentId());
        if (parent == null) {
            entity.setParentId(TreeUtil.ROOT_KEY);
            entity.setAncestors(TreeUtil.ROOT_KEY + ",");
        }
        if (parent != null) {
            if (!MenuType.isDir(parent.getMenuType())) {
                throw BizException.of("菜单类型的上级只能是目录");
            }
            entity.setAncestors(parent.getAncestors() + entity.getParentId() + ",");
        }
        this._menu.newQuery()
                .eq(Menu::getAppCode, entity.getAppCode())
                .eq(Menu::getParentId, entity.getParentId())
                .eq(Menu::getMenuName, entity.getMenuName())
                .ne(Menu::getMenuId, entity.getMenuId())
                .existsThenThrow("重复的名称:{}", entity.getMenuName());
        this._menu.update(entity);
        this.updateDescendants(entity);
    }


    /**
     * 更新按钮
     *
     * @param old    老
     * @param entity 实体
     */
    private void updateButton(Menu old, Menu entity) {
        if (this.isBasePerm(old)) {
            throw BizException.of("基础权限按钮不允许修改");
        }
        entity.copyBaseField(old);
        entity.setMenuId(old.getMenuId());
        entity.setAppCode(old.getAppCode());
        entity.setMenuType(old.getMenuType());
        entity.setPerms(old.getPerms());
        entity.setParentId(old.getParentId());
        entity.setAncestors(old.getAncestors());
        entity.setIsCache(null);
        entity.setIsVisible(null);
        entity.setIsFrame(null);
        entity.setIcon(null);
        entity.setPath(null);
        entity.setComponent(null);
        entity.setQuery(null);
        ValidUtil.valid(entity, Menu.ButtonGroup.class);
        this._menu.newQuery()
                .eq(Menu::getAppCode, entity.getAppCode())
                .eq(Menu::getParentId, entity.getParentId())
                .eq(Menu::getMenuName, entity.getMenuName())
                .ne(Menu::getMenuId, entity.getMenuId())
                .existsThenThrow("重复的名称:{}", entity.getMenuName());
        this._menu.update(entity);
    }


    public void delete(String menuId) {
        Menu menu = this._menu.getById(menuId);
        if (menu == null) {
            throw BizException.of("无效的菜单主键:{}", menuId);
        }
        MenuType menuType = menu.getMenuType();
        String perms = menu.getPerms().orEmpty();
        if (MenuType.isButton(menuType)) {
            if (this.isBasePerm(menu)) {
                throw BizException.of("基础权限按钮不允许删除");
            }
            this._menu.deleteById(menuId);
            this._role_menu.deleteBy(RoleMenu::getMenuId, menuId);
            return;
        }

        if (MenuType.isMenu(menuType)) {
            List<String> menuIds = this._menu.newQuery()
                    .eq(Menu::getParentId, menuId)
                    .toList(Menu::getMenuId);
            menuIds.add(menuId);
            this._menu.deleteByIds(menuIds);
            this._role_menu.newUpdate()
                    .in(RoleMenu::getMenuId, menuIds)
                    .remove();
            return;
        }

        if (MenuType.isDir(menuType)) {
            this._menu.newQuery()
                    .eq(Menu::getParentId, menuId)
                    .existsThenThrow("此目录下存在子目录或者子菜单,不允许删除");
            this._menu.deleteById(menuId);
            this._role_menu.deleteBy(RoleMenu::getMenuId, menuId);
        }
    }

    /**
     * 更新后代
     *
     * @param entity 实体
     */
    public void updateDescendants(Menu entity) {
        String ancestors = entity.getAncestors();
        String menuId = entity.getMenuId();
        String separator = "," + menuId + ",";
        List<Menu> descendants = this._menu.newQuery()
                .like(Menu::getAncestors, separator)
                .stream()
                .peek(descendant -> {
                    String oldAncestors = descendant.getAncestors();
                    String before = StrUtil.subBefore(oldAncestors, separator, false);
                    String searchStr = before + separator;
                    String replacement = ancestors + menuId + ",";
                    String newAncestors = StrUtil.replace(oldAncestors, searchStr, replacement);
                    descendant.setAncestors(newAncestors);
                })
                .collect(Collectors.toList());
        this._menu.updateBatch(descendants);
    }

    private boolean isBasePerm(Menu menu) {
        return BASE_PERMS.keySet()
                .stream()
                .anyMatch(key -> menu.getPerms().orEmpty().endsWith(":" + key));
    }
}
