package github.shinyoki.bluemap.xjcraft.manager;

import github.shinyoki.bluemap.xjcraft.XJCraftBaseHomeBlueMapDrawer;
import github.shinyoki.bluemap.xjcraft.marker.MarkerCreator;
import github.shinyoki.bluemap.xjcraft.marker.strategy.ICreator;
import github.shinyoki.bluemap.xjcraft.utils.Log;
import org.bukkit.Bukkit;

import java.util.Objects;

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

    /**
     * 修改配置文件后，重新加载配置
     */
    public static void reloadConfig() {
        Bukkit.getScheduler().runTask(XJCraftBaseHomeBlueMapDrawer.getInstance(), () -> {
            getBlueMapManager().reloadConfig();
            Log.info("重新读取配置文件成功！");

            Bukkit.getScheduler().runTaskAsynchronously(XJCraftBaseHomeBlueMapDrawer.getInstance(), () -> {
                // 重新渲染
                getBlueMapManager().renderAll();
                Log.info("重新渲染Markers完成！");
            });

        });
    }

    /**
     * 修改Marker的生成策略
     */
    public static void changeMarkerCreator(MarkerCreator markerCreator) {
        getBlueMapManager().changeMarkerCreator(markerCreator);
    }

}
