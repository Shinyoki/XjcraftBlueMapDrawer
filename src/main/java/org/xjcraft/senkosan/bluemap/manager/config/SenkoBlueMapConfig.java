package org.xjcraft.senkosan.bluemap.manager.config;

/**
 * 配置类
 *
 * @author senko
 * @date 2022/9/15 12:11
 */
public class SenkoBlueMapConfig {

    /**
     * 默认值
     */
    public static class DEFAULT_VALUE {

        public static String TOWN_CMD_LOCATION = "town-cmdlocation";
        public static String BASE_CMD_LOCATION = "base-cmdlocation";
        public static String APPLY_PLAYER_STATUS_FOLDER = "status";
        public static Boolean DEFAULT_SHOW_HOME = Boolean.FALSE;
        public static Boolean DEFAULT_SHOW_BASE = Boolean.TRUE;

    }

    /**
     * 配置前缀
     */
    public static class KEY {

        public static final String DEFAULT_SHOW_HOME = "senko.render.default-show-home";        // 是否默认渲染家的图标
        public static final String DEFAULT_SHOW_BASE = "senko.render.default-show-base";        // 是否默认渲染基地的图标
        public static final String APPLY_PLAYER_STATUS_FOLDER = "senko.xjcraft.apply-player-meta-path"; // 存放玩家认证信息文件的所在文件夹相对路径 如 status
        public static final String TOWN_CMD_LOCATION = "senko.xjcraft.town-cmdlocation";         // 认证玩家的家命令方块坐标的key
        public static final String BASE_CMD_LOCATION = "senko.xjcraft.base-cmdlocation";         // 认证玩家的基地命令方块坐标的key
        public static final String DEBUG_MODE = "senko.debug-mode";                             // 是否开启调试模式

    }

}
