package easier.framework.test.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseEntity;
import easier.framework.core.plugin.jackson.annotation.ShowDictDetail;
import easier.framework.core.plugin.tree.TreeBuilder;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.core.util.TreeUtil;
import easier.framework.test.enums.EnableStatus;
import easier.framework.test.enums.MenuType;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 菜单表
 * t_menu
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "t_menu", comment = "菜单表")
public class Menu extends BaseEntity {

    @Column(comment = "父菜单主键")
    @NotBlank(groups = ButtonGroup.class)
    private String parentId;
    @Column(comment = "权限标识")
    @Size(max = 100)
    @NotBlank(groups = {MenuGroup.class, ButtonGroup.class})
    private String perms;
    @Column(comment = "路由地址")
    @Size(max = 255)
    @NotBlank(groups = {DirGroup.class, MenuGroup.class})
    private String path;

    @TableField(exist = false)
    public static TreeBuilder<Menu> treeBuilder = TreeBuilder
            .of(Menu.class)
            .key(Menu::getMenuId)
            .name(Menu::getMenuName)
            .parentKey(Menu::getParentId)
            .sort(Menu::getSort)
            .enable(it -> EnableStatus.isEnable(it.getStatus()))
            .build();

    @Column(comment = "菜单主键")
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String menuId;

    @Column(comment = "所属应用编码", notNull = true)
    @NotBlank
    private String appCode;

    @Column(comment = "菜单类型", notNull = true)
    @NotNull
    @ShowDictDetail
    private MenuType menuType;

    @Column(comment = "菜单名称")
    @NotBlank
    @Size(min = 2, max = 50)
    private String menuName;

    @Column(comment = "组件路径")
    @Size(max = 255)
    @NotBlank(groups = MenuGroup.class)
    private String component;

    @Column(comment = "祖先列表", notNull = true, length = 1024)
    private String ancestors;

    @Column(comment = "排序", notNull = true, defaultValue = TreeUtil.DEFAULT_SORT_STR)
    @NotNull
    private Integer sort;

    @Column(comment = "状态")
    @NotNull
    @ShowDictDetail
    private EnableStatus status;

    @Column(comment = "菜单图标")
    private String icon;

    @Column(comment = "是否为外链")
    @NotNull(groups = {DirGroup.class, MenuGroup.class})
    private Boolean isFrame;

    @Column(comment = "是否可显示")
    @NotNull(groups = {DirGroup.class, MenuGroup.class})
    private Boolean isVisible;

    @Column(comment = "是否缓存")
    @NotNull(groups = MenuGroup.class)
    private Boolean isCache;

    @Column(comment = "路由参数")
    private String query;

    public interface DirGroup {
    }

    public interface MenuGroup {
    }

    public interface ButtonGroup {
    }
}
