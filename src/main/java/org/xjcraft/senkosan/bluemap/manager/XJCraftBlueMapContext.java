package org.xjcraft.senkosan.bluemap.manager;

import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.marker.MarkerCreator;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * @author senko
 * @date 2022/8/14 9:44
 */
public class XJCraftBlueMapContext {
    private volatile static DefaultBlueMapManager blueMapManager;

    public static DefaultBlueMapManager getBlueMapManager() {
        if (Objects.isNull(blueMapManager)) {
            synchronized (XJCraftBlueMapContext.class) {
                if (Objects.isNull(blueMapManager)) {
                    blueMapManager = new DefaultBlueMapManager();
                }
            }
        }
        return blueMapManager;
    }

    public static void resetManager() {
        blueMapManager = null;
    }

    /**
     * 修改配置文件后，重新加载配置
     */
    public static void reloadConfig() {
        resetManager();
        DefaultBlueMapManager blueMapManager = getBlueMapManager();
        blueMapManager.reloadConfig();

        XJCraftBaseHomeBlueMapDrawer plugin = XJCraftBaseHomeBlueMapDrawer.getInstance();
        if (blueMapManager.isEnableOnlinePlayerRender()) {
            // 开启在线渲染
            ScheduledFuture<?> scheduledFuture = plugin.getScheduledFuture();
            if (Objects.isNull(scheduledFuture) || scheduledFuture.isCancelled()) {
                plugin.scheduleOnlinePlayerRender();
            }
        } else {
            // 关闭在线渲染
            blueMapManager.clearAllOnlinePlayerMarkers();
            XJCraftBaseHomeBlueMapDrawer.clearOnlinePlayerCache();
            ScheduledFuture<?> scheduledFuture = plugin.getScheduledFuture();
            if (Objects.nonNull(scheduledFuture) && !scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
        }
        Log.info("重新读取配置文件完成！");
    }

    /**
     * 修改Marker的生成策略
     */
    public static void changeMarkerCreator(MarkerCreator markerCreator) {
        getBlueMapManager().changeMarkerCreator(markerCreator);
    }

}
