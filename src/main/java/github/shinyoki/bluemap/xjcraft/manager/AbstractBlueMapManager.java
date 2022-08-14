package github.shinyoki.bluemap.xjcraft.manager;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.marker.MarkerAPI;
import github.shinyoki.bluemap.xjcraft.exception.XBMPluginException;
import github.shinyoki.bluemap.xjcraft.utils.Log;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.apply.ApplyHelper;

import java.io.IOException;
import java.util.Optional;

/**
 * @author senko
 * @date 2022/8/13 14:38
 */
public abstract class AbstractBlueMapManager {

    private MarkerAPI markerAPI;

    private BlueMapMap map;

    /**
     * BlueMapAPI在2.0版本里存在很多破坏性的更新，
     * 需要适配
     */
    private Integer blueMapAPIVersion = 1;

    public Integer getBlueMapAPIVersion() {
        return blueMapAPIVersion;
    }

    public AbstractBlueMapManager() {
        this(AuditPlugin.getPlugin().getHelper(), BlueMapAPI.getInstance().get());
    }

    /**
     * @param applyHelper 小鸡服认证插件的ApplyHelper，用于获取玩家的meta信息
     * @param api         BlueMapAPI，用于渲染Marker
     */
    public AbstractBlueMapManager(ApplyHelper applyHelper, BlueMapAPI api) {

        try {
            this.markerAPI = Optional.ofNullable(api).orElseThrow(() -> new XBMPluginException("未能正确获取BlueMap相关API，请检查是否正确加载了BlueMap插件")).getMarkerAPI();
            this.map = api.getMap("world").get();
            this.blueMapAPIVersion = Integer.parseInt(api.getBlueMapVersion().substring(0, api.getBlueMapVersion().indexOf(".")));
        } catch (IOException e) {
            Log.error("加载BlueMapAPI时发生错误!");
            e.printStackTrace();
        }

    }

    public MarkerAPI getMarkerAPI() {
        return markerAPI;
    }

    public BlueMapMap getMap() {
        return map;
    }

    public void renderMarker(Player player) {
        this.renderMarker(player);
    }

    /**
     * 给指定玩家渲染Marker
     */
    public abstract void renderMarker(String playerName);

}
