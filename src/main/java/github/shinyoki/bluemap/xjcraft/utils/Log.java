package github.shinyoki.bluemap.xjcraft.utils;

import github.shinyoki.bluemap.xjcraft.XJCraftBaseHomeBlueMapDrawer;

/**
 * 快速输出插件日志
 *
 * @author senko
 * @date 2022/8/13 14:36
 */
public class Log {

    /**
     * 级别：INFO
     */
    public static void info(String message) {
        XJCraftBaseHomeBlueMapDrawer.getInstance().getLogger().info(message);
    }

    /**
     * 级别：WARNING
     */
    public static void warning(String message) {
        XJCraftBaseHomeBlueMapDrawer.getInstance().getLogger().warning(message);
    }

    /**
     * 级别：SEVERE
     */
    public static void error(String message) {
        XJCraftBaseHomeBlueMapDrawer.getInstance().getLogger().severe(message);
    }

}
