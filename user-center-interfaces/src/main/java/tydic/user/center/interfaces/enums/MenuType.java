package tydic.user.center.interfaces.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import easier.framework.core.plugin.dict.Dict;
import easier.framework.core.plugin.enums.EnumDesc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 菜单类型
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Getter
@RequiredArgsConstructor
@Dict(code = "menu_type", name = "菜单类型")
public enum MenuType {
    dir("dir", "目录"),
    menu("menu", "菜单"),
    button("button", "按钮");
    @EnumValue
    private final String value;
    @EnumDesc
    private final String desc;

    public static boolean isDir(MenuType input) {
        return MenuType.dir.equals(input);
    }

    public static boolean isMenu(MenuType input) {
        return MenuType.menu.equals(input);
    }

    public static boolean isButton(MenuType input) {
        return MenuType.button.equals(input);
    }
}
