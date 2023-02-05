package org.xjcraft.senkosan.bluemap.utils;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.WebApp;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

/**
 * 有关BlueMap的工具类
 *
 * @author senko
 * @date 2022/8/13 19:21
 */
public class BlueMapUtil {

    public static BlueMapAPI getBlueMapAPI() {
        return BlueMapAPI.getInstance()
                .get();
    }

    public static WebApp getWebApp() {
        return getBlueMapAPI().getWebApp();
    }

    /**
     * 获取Marker Label
     *
     * @param playerName 玩家名
     * @param markerType 标签类型
     */
    protected static String getLabel(String playerName, MarkerType markerType) {
        switch (markerType) {
            case BASE:
                return playerName + "的基地";
            case HOME:
                return playerName + "的家";
        }
        return "";
    }

    /**
     * 获取BlueMap中图标资源对应的存储路径
     */
    public static String getBlueMapIcon(BaseIcon icon) {
        return "data/images/" + icon.getIconName() + ".png";
    }

    /**
     * 生成玩家头像HTML模板，需要发送HTTTP请求查询
     * @param playerName    玩家名
     * @return              HTML模板
     */
    public static String getOnlinePlayerIconHtml(String playerName) {
        String playerHeadUrl = MojangUtil.getPlayerHeadUrl(playerName);
        return htmlTemplate(playerName, playerHeadUrl);
    }

    /**
     * 不需要HTTP请求，但是得确保UUID为正确的
     * @param playerName    玩家名
     * @param uuid          正版UUID
     * @return              HTML模板
     */
    public static String getOnlinePlayerIconHtml(String playerName, String uuid) {
        String playerHeadUrl = null;
        if (uuid == null || uuid.trim().equals("")) {
            // BlueMap默认的头像，文件位于 服务端/bluemap/web/ 文件夹
            playerHeadUrl =  "assets/steve.png";
        } else {
            playerHeadUrl = uuid;
        }

        return htmlTemplate(playerName, playerHeadUrl);
    }

    /**
     * <html>
     *     <div class="bm-marker-player" distance-data="near">
     *         <img draggable="false" alt="玩家名" src="https://crafatar.com/avatars/正版玩家UUID"/>
     *         <div class="bm-player-name">玩家名</div>
     *     </div>
     * </html>
     * @param playerName        玩家名
     * @param uuid              正版玩家UUID
     * @return                  HTMLMarker模板
     */
    private static String htmlTemplate(String playerName, String uuid) {
        // distance-data 是BlueMap前端展示Players Marker的Div元素中的属性，Web会针对地图的缩放比例自动调整合适的值
        // 目前没找到如何让自定义的Marker HTML元素也受前端同步控制，只能先写死一个固定值了
        return "<div class=\"bm-marker-player\" distance-data=\"near\">\n" +
                "  <img draggable=\"false\" alt=\"" + playerName + "\" src=\"" + MojangUtil.PLAYER_HEAD_API + uuid + "\"/>\n" +
                "  <div class=\"bm-player-name\">" + playerName + "</div>\n" +
                "</div>";
    }

    public static void checkMapIcon(BaseIcon icon) {
        Path webRoot = getBlueMapAPI().getWebApp()
                .getWebRoot();
        // 判断路径下是否存在 "data/images/icon.png" 文件
        String iconPath = "data/images/" + icon.getIconName() + ".png";
        boolean exists = webRoot.resolve(iconPath).toFile()
                .exists();


        if (!exists) {
            Log.info("未能在BlueMap的web文件中找到 " + iconPath + " ，开始尝试从插件资源中复制");
            // 如果不存在，则将插件中的资源文件复制到该路径下
            XJCraftBaseHomeBlueMapDrawer.submit(() -> {
                try (InputStream inputStream = XJCraftBaseHomeBlueMapDrawer.getSpigotClassLoader().getResourceAsStream(icon.getResourceSrc())) {
                    if (Objects.nonNull(inputStream)) {
                        // 根据path存储图片
                        ImageIO.write(ImageIO.read(inputStream), "png", webRoot.resolve(iconPath)
                                .toFile());
                    } else {
                        Log.error("获取插件的resource流失败！");
                    }
                } catch (IOException e) {
                    Log.error("复制文件失败！");
                    e.printStackTrace();
                }
            });
        }
    }


    public enum BaseIcon {
        // 家
        HOME("HOME", "images/house.png", "house"),
        // 基地
        BASE("BASE", "images/basement.png", "basement"),
        DEFAULT("DEFAULT", "images/basement.png", "basement");
        private final String icon;
        private final String resourceSrc;
        private final String iconName;

        public String getIconName() {
            return iconName;
        }

        public String getResourceSrc() {
            return resourceSrc;
        }

        public String getIcon() {
            return icon;
        }

        BaseIcon(String icon, String src, String iconName) {
            this.icon = icon;
            this.resourceSrc = src;
            this.iconName = iconName;
        }
    }

}
