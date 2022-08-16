package org.xjcraft.senkosan.bluemap.manager;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.marker.Marker;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.PlayerMeta;
import org.jim.bukkit.audit.apply.ApplyHelper;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.constants.MapConstants;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.util.Optional;
import java.util.concurrent.Future;

/**
 * 提供获取基地以及家的坐标，以及默认配置
 *
 * @author senko
 * @date 2022/8/13 15:52
 */
public abstract class ConfigurableBlueMapManager extends AbstractBlueMapManager {

    private ApplyHelper applyHelper;
    private String basePrefix = "senko.render.";
    private String homeLocationKey = MapConstants.XJCRAFT_HOME_TAG;
    private String baseLocationKey = MapConstants.XJCRAFT_BASE_TAG;
    private boolean defaultShowHome = false;
    private boolean defaultShowBase = true;
    private FileConfiguration configuration;


    public ConfigurableBlueMapManager(FileConfiguration config) {
        super();
        initKey(config);
        this.applyHelper = AuditPlugin.getPlugin().getHelper();
    }

    public ConfigurableBlueMapManager(ApplyHelper applyHelper, BlueMapAPI api, FileConfiguration config) {
        super(applyHelper, api);
        initKey(config);
        this.applyHelper = applyHelper;
    }

    public void reloadConfig() {

        Log.info("重新读取配置文件...");

        // 重载
        XJCraftBaseHomeBlueMapDrawer plugin = XJCraftBaseHomeBlueMapDrawer.getInstance();
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        // 修改配置
        initKey(cfg);
        Log.setIsDebugMode(cfg.getBoolean("senko.debug-mode"));

    }

    /**
     * 初始化参数：
     * basePrefix：config.yml中的配置前缀，默认为"senko.render"
     * homeLocationKey：获取认证玩家的命令方块坐标的key，默认为"town-cmdlocation"
     * baseLocationKey：获取认证玩基地的命令方块坐标的key，默认为"base-cmdlocation"
     * allInOne：是否将基地和家的Marker合并，默认为false
     * defaultShowHome：是否显示家（小镇）的Marker，默认为false，false是为了放置占满屏幕
     * defaultShowBase：是否显示基地，默认为true
     */
    private void initKey(FileConfiguration pluginConfig) {

        try {
            this.homeLocationKey = Optional.ofNullable(pluginConfig.getString(basePrefix + homeLocationKey))
                    .orElseThrow(() -> new XBMPluginException("未能正确获取配置文件中的配置项：" + basePrefix + homeLocationKey));
            this.baseLocationKey = Optional.ofNullable(pluginConfig.getString(basePrefix + baseLocationKey))
                    .orElseThrow(() -> new XBMPluginException("未能正确获取配置文件中的配置项：" + basePrefix + baseLocationKey));
            this.defaultShowHome = Optional.ofNullable(pluginConfig.getBoolean(basePrefix + ".default-show-home"))
                    .orElse(false);
            this.defaultShowBase = Optional.ofNullable(pluginConfig.getBoolean(basePrefix + ".default-show-base"))
                    .orElse(true);
            this.configuration = pluginConfig;
        } catch (XBMPluginException e) {
            Log.warning(e.getMessage());
        }

    }

    public PlayerMeta getPlayerMeta(String playerName) {
        return getApplyHelper().getPlayerMeta(playerName);
    }

    public PlayerMeta getPlayerMeta(Player player) {
        return getApplyHelper().getPlayerMeta(player);
    }

    public ApplyHelper getApplyHelper() {
        return applyHelper;
    }

    /**
     * 获取基地的MarkerSet
     */
    public MarkerSet getBaseMarkerSet() {

        return getMarkerAPI().getMarkerSet(MapConstants.BASE_MARKER_SET_ID)
                .orElseGet(() -> {
                    MarkerSet ms = getMarkerAPI().createMarkerSet(MapConstants.BASE_MARKER_SET_ID);
                    ms.setDefaultHidden(!isDefaultShowBase());
                    ms.setToggleable(true);
                    ms.setLabel(MapConstants.BASE_MARKER_SET_LABEL);
                    return ms;
                });

    }

    /**
     * 获取玩家小镇的MarkerSet
     */
    public MarkerSet getHomeMarkerSet() {

        return getMarkerAPI().getMarkerSet(MapConstants.HOME_MARKER_SET_ID)
                .orElseGet(() -> {
                    MarkerSet ms = getMarkerAPI().createMarkerSet(MapConstants.HOME_MARKER_SET_ID);
                    ms.setToggleable(true);
                    ms.setDefaultHidden(!isDefaultShowHome());
                    ms.setLabel(MapConstants.HOME_MARKER_SET_LABEL);
                    return ms;
                });

    }

    public Location getBaseLocation(Player player) {
        return getBaseLocation(player.getName());
    }

    public Location getHomeLocation(Player player) {
        return getHomeLocation(player.getName());
    }

    public Location getBaseLocation(String playerName) {
        return getPlayerMeta(playerName).getLocation(baseLocationKey);
    }

    public Location getHomeLocation(String playerName) {
        return getPlayerMeta(playerName).getLocation(homeLocationKey);
    }

    public boolean isDefaultShowHome() {
        return defaultShowHome;
    }

    public boolean isDefaultShowBase() {
        return defaultShowBase;
    }

    public Future<?> renderMarkerAsynchronously(String playerName) {
        return XJCraftBaseHomeBlueMapDrawer.getExecutorService()
                .submit(() -> {
                    renderMarker(playerName);
                });
    }

    @Override
    public void renderMarker(String playerName) {

        // 如果玩家还未认证通过，则一定没有基地和小镇
        if (false == getApplyHelper().isApply(playerName)) {
            return;
        }

        // 如果认证通过却没有设置基地和小镇，则坐标不存在，此时不会创建Marker
        for (MarkerSet markerSet : getRenderMarkerSets()) {

            // 如果是基地的MarkerSet，则渲染基地的Marker
            if (markerSet.getId().equals(MapConstants.BASE_MARKER_SET_ID)) {
                createBaseMarker(playerName, markerSet);
            }

            // 如果是小镇的MarkerSet，则渲染小镇的Marker
            else if (markerSet.getId().equals(MapConstants.HOME_MARKER_SET_ID)) {
                createHomeMarker(playerName, markerSet);
            }

        }

    }

    /**
     * 获取MarkerSet，如果是单独显示，则返回一个MarkerSet，
     * 否则返回两个MarkerSet，分别用于展示基地和小镇的Marker
     */
    protected MarkerSet[] getRenderMarkerSets() {
        return new MarkerSet[]{getBaseMarkerSet(), getHomeMarkerSet()};
    }

    protected void createBaseMarker(String playerName, MarkerSet markerSet) {
        Optional.ofNullable(getBaseLocation(playerName))
                .ifPresent(baseLocation -> {
                    // 有基地
                    createMarker(playerName, baseLocation, MarkerType.BASE, markerSet);
                });
    }

    protected void createHomeMarker(String playerName, MarkerSet markerSet) {
        Optional.ofNullable(getHomeLocation(playerName))
                .ifPresent(homeLocation -> {
                    // 有小镇
                    createMarker(playerName, homeLocation, MarkerType.HOME, markerSet);
                });
    }

    /**
     * 创建Marker
     *
     * @param playerName 玩家名称
     * @param location   基地坐标
     * @param markerType Marker类型：{@link MarkerType}
     * @param markerSet  MarkerSet
     */
    protected abstract Marker createMarker(String playerName, Location location, MarkerType markerType, MarkerSet markerSet);

}
