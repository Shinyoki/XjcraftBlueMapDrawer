package org.xjcraft.senkosan.bluemap.manager;

import de.bluecolored.bluemap.api.markers.MarkerSet;
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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.xjcraft.senkosan.bluemap.manager.config.SenkoBlueMapConfig.*;
import static org.xjcraft.senkosan.bluemap.manager.config.SenkoBlueMapConfig.KEY.DEBUG_MODE;

/**
 * 提供获取基地以及家的坐标，以及默认配置
 *
 * @author senko
 * @date 2022/8/13 15:52
 */
public abstract class ConfigurableBlueMapManager extends AbstractMarkerManager {

    // ApplyHelper：用于获取玩家的坐标信息
    private final ApplyHelper applyHelper;
    // applyHelper中记录玩家 家命令方块坐标的key
    private String homeLocationKey;
    // applyHelper中记录玩家 基地命令方块坐标的key
    private String baseLocationKey;
    // 是否自动渲染 家
    private boolean defaultShowHome;
    // 是否自动渲染 基地
    private boolean defaultShowBase;
    // applyHelper中存放玩家信息文件的所在文件夹相对路径
    private String playerMetaFolder;

    public ConfigurableBlueMapManager() {
        this.applyHelper = AuditPlugin.getPlugin()
                .getHelper();
        this.initKey(XJCraftBaseHomeBlueMapDrawer.getInstance().getConfig());
    }

    /**
     * 重新加载生效配置
     */
    public void reloadConfig() {

        Log.info("重新读取配置文件...");

        // 重载
        XJCraftBaseHomeBlueMapDrawer plugin = XJCraftBaseHomeBlueMapDrawer.getInstance();
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();
        Log.info("重新配置Api...");
        this.resetApiAndMapToRender();

        // 修改配置
        initKey(cfg);
        Log.setIsDebugMode(cfg.getBoolean(DEBUG_MODE));

    }

    /**
     * 获取已有记录的玩家名
     */
    @Override
    public List<String> getPlayersToRender() {
        // 获取AuditPlugin配置文件夹 status
        String statusDirPath = AuditPlugin.getPlugin().getDataFolder().getAbsolutePath()
                .concat(File.separator).concat(playerMetaFolder);
        File statusDir = new File(statusDirPath);
        if (!statusDir.exists()) {
            statusDir.mkdirs();
            return null;
        }

        // 遍历所有内部的文件名
        return Arrays.stream(statusDir.listFiles())
                // 是yml文件
                .filter(file -> file.isFile() && file.getName().endsWith(".yml"))
                // 获取玩家名（文件名）
                .map(file -> file.getName().split("\\.")[0])
                .collect(Collectors.toList());
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
            this.homeLocationKey = Optional.ofNullable(pluginConfig.getString(KEY.TOWN_CMD_LOCATION))
                    .orElse(DEFAULT_VALUE.TOWN_CMD_LOCATION);
            this.baseLocationKey = Optional.ofNullable(pluginConfig.getString(KEY.BASE_CMD_LOCATION))
                    .orElse(DEFAULT_VALUE.BASE_CMD_LOCATION);
            this.playerMetaFolder = Optional.ofNullable(pluginConfig.getString(KEY.APPLY_PLAYER_STATUS_FOLDER))
                    .orElse(DEFAULT_VALUE.APPLY_PLAYER_STATUS_FOLDER);
            this.defaultShowHome = Optional.of(pluginConfig.getBoolean(KEY.DEFAULT_SHOW_HOME))
                    .orElse(DEFAULT_VALUE.DEFAULT_SHOW_HOME);
            this.defaultShowBase = Optional.of(pluginConfig.getBoolean(KEY.DEFAULT_SHOW_BASE))
                    .orElse(DEFAULT_VALUE.DEFAULT_SHOW_BASE);
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

        createBaseMarker(playerName);
        createHomeMarker(playerName);
    }

    protected void createBaseMarker(String playerName) {
        Optional.ofNullable(getBaseLocation(playerName))
                .ifPresent(baseLocation -> {
                    // 有基地
                    createMarker(playerName, baseLocation, MarkerType.BASE, getBaseMarkerSet());
                });
    }

    protected void createHomeMarker(String playerName) {
        Optional.ofNullable(getHomeLocation(playerName))
                .ifPresent(homeLocation -> {
                    // 有小镇
                    createMarker(playerName, homeLocation, MarkerType.HOME, getHomeMarkerSet());
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
    protected abstract void createMarker(String playerName, Location location, MarkerType markerType, MarkerSet markerSet);

    @Override
    protected MarkerSet buildMarkerSet(MarkerType markerType) {
        // 如果不存在该标点集合，则创建一个新的标点集合
        MarkerSet markerSet = MarkerSet.builder()
                .defaultHidden(markerType.equals(MarkerType.HOME) ? !defaultShowHome : !defaultShowBase)
                .label(markerType.getMarkerSetLabel())
                .toggleable(true)
                .build();
        getMapToRender().getMarkerSets()
                .put(markerType.getMarkerSetId(), markerSet);
        return markerSet;
    }

}
