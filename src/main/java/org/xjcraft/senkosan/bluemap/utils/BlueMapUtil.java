package org.xjcraft.senkosan.bluemap.utils;

import de.bluecolored.bluemap.api.BlueMapAPI;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 有关BlueMap的工具类
 *
 * @author senko
 * @date 2022/8/13 19:21
 */
public class BlueMapUtil {

    public static BlueMapAPI getBlueMapAPI() {
        return BlueMapAPI.getInstance().get();
    }


    /**
     * 获取BlueMap中图标资源对应的存储路径
     */
    public static String getBlueMapIcon(BaseIcon icon) {

        try {
            Optional<String> iconSrc = getBlueMapAPI().availableImages().entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(icon.getIcon()))
                    .map(Map.Entry::getValue)
                    .findFirst();
            if (iconSrc.isPresent()) {
                return iconSrc.get();
            } else {
                return registerImage(icon);
            }
        } catch (IOException e) {
            XJCraftBaseHomeBlueMapDrawer.getInstance().getLogger().severe("获取图标失败：" + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    /**
     * 将插件里的图标文件复制到BlueMap的图标文件夹中
     */
    private static String registerImage(BaseIcon icon) {
        InputStream is = null;
        try {
            is = XJCraftBaseHomeBlueMapDrawer.getInstance()
                    .getResource(icon.getDefaultSrc());
            return getBlueMapAPI().createImage(ImageIO.read(is), icon.getIcon());
        } catch (IOException e) {
            Log.warning("注册图片失败：" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(is)) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public enum BaseIcon {
        // 家
        HOME("HOME", "house.png"),
        // 基地
        BASE("BASE", "house.png"),
        DEFAULT("DEFAULT", "house.png");
        private String icon;
        private String defaultSrc;

        public String getDefaultSrc() {
            return defaultSrc;
        }

        public String getIcon() {
            return icon;
        }

        BaseIcon(String icon, String defaultSrc) {
            this.icon = icon;
            this.defaultSrc = defaultSrc;
        }
    }

}
