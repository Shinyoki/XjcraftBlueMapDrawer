package org.xjcraft.senkosan.bluemap.utils;

import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;

/**
 * 快速输出插件日志
 *
 * @author senko
 * @date 2022/8/13 14:36
 */
public class Log {

    private static boolean isDebugMode = false;

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

    public static void d(String message) {
        debug(message);
    }

    public static void debug(String message) {
        if (isDebugMode) {
            info(message);
        }
        // do-nothing
    }

    public static boolean isIsDebugMode() {
        return isDebugMode;
    }

    public static void setIsDebugMode(boolean isDebugMode) {
        Log.isDebugMode = isDebugMode;
    }
}
