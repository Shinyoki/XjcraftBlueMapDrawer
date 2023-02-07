package org.xjcraft.senkosan.bluemap.manager;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.xjcraft.senkosan.bluemap.enums.MarkerType.*;

/**
 * 标点管理器
 *
 * @author senko
 * @date 2022/9/15 11:16
 */
public abstract class AbstractMarkerManager implements IRenderOnlinePlayerMarker {

    private BlueMapAPI mapApi;

    private BlueMapMap mainLandMap;

    private MarkerSet baseMarkerSet;

    private MarkerSet homeMarkerSet;

    public AbstractMarkerManager() {
        this.resetApiAndMapToRender();
    }

    /**
     * 重置API和地图
     */
    protected void resetApiAndMapToRender() {
        this.mapApi = BlueMapAPI.getInstance().orElseThrow(() -> {
            Bukkit.getPluginManager().disablePlugin(XJCraftBaseHomeBlueMapDrawer.getInstance());
            return new XBMPluginException("未能正确获取BlueMap相关API，请检查是否正确加载了BlueMap插件");
        });

        this.mainLandMap = mapApi.getMap("mainland").orElseThrow(() -> {
            Bukkit.getPluginManager().disablePlugin(XJCraftBaseHomeBlueMapDrawer.getInstance());
            return new XBMPluginException("未能正确获取BlueMap地图，请检查是否正确加载了BlueMap插件");
        });
        this.baseMarkerSet = buildHomeBaseMarkerSet(BASE);
        this.homeMarkerSet = buildHomeBaseMarkerSet(HOME);

    }

    /**
     * 获取或创建一个对应类型的MarkerSet
     * @param markerType    Marker类型
     * @return              MarkerSet
     */
    public MarkerSet getHomeBaseMarkerSet(MarkerType markerType) {
        switch (markerType) {
            case BASE:
                return baseMarkerSet;
            case HOME:
                return homeMarkerSet;
            default:
                throw new XBMPluginException("不支持的Marker类型");
        }
    }

    /**
     * 重置markerSet
     */
    public void resetHomeBaseMarkerSet(MarkerType markerType) {
        MarkerSet markerSet = getHomeBaseMarkerSet(markerType);
        if (Objects.nonNull(markerSet)) {
            markerSet.getMarkers()
                    .clear();
        }
    }

    public void renderAllHomeBaseMarker(final CommandSender sender) {


        XJCraftBaseHomeBlueMapDrawer.submit(() -> {
            long beforeIterateOffline = System.currentTimeMillis();
            try {
                List<String> playerNames = getPlayersToRender();
                Log.d("将被渲染玩家数量:" + playerNames.size());
                Log.info("获取需被渲染玩家数量耗时：" + (System.currentTimeMillis() - beforeIterateOffline) + "ms");

                long beforeRender = System.currentTimeMillis();
                // 在开始渲染钱
                playerNames.forEach(this::renderHomeBaseMarker);
                Log.d("渲染所有标点花费的时间：" + (System.currentTimeMillis() - beforeRender) + "ms");

                long beforeSave = System.currentTimeMillis();

                Log.d("保存所有标点花费的时间：" + (System.currentTimeMillis() - beforeSave) + "ms");
            } catch (Exception e) {
                Log.error("渲染所有人的Markers失败！" + e.getMessage());
                e.printStackTrace();
            }
            Log.info("渲染Markers总耗时：" + (System.currentTimeMillis() - beforeIterateOffline) + "ms");

            if (Objects.nonNull(sender)) {
                sender.sendMessage("渲染Markers完成！总共耗时：" + (System.currentTimeMillis() - beforeIterateOffline) + "ms");
            }

        });

    }

    /**
     * 只修改坐标
     * @param marker        Marker
     * @param newLocation   新的坐标
     */
    protected void generalEdit(Marker marker, Location newLocation) {
        Optional.ofNullable(marker)
                .ifPresent(oldMarker -> {
                    // location
                    oldMarker.setPosition(new Vector3d(newLocation.getX() + .5D, newLocation.getY() + .5D, newLocation.getZ() + .5D));
                });
    }

    // getters & setters
    public MarkerSet getMarkerSetToRender(MarkerType markerType) {
        return markerType.equals(BASE) ? getBaseMarkerSet() : getHomeMarkerSet();
    }
    public MarkerSet getHomeMarkerSet() {
        return getHomeBaseMarkerSet(HOME);
    }

    public MarkerSet getBaseMarkerSet() {
        return getHomeBaseMarkerSet(BASE);
    }

    public BlueMapAPI getMapApi() {
        return mapApi;
    }

    public BlueMapMap getMainLandMap() {
        return mainLandMap;
    }

    public void setMapApi(BlueMapAPI mapApi) {
        this.mapApi = mapApi;
    }

    public void setMainLandMap(BlueMapMap mainLandMap) {
        this.mainLandMap = mainLandMap;
    }

    public abstract void renderHomeBaseMarker(String playerName);
    // abstract methods
    protected abstract MarkerSet buildHomeBaseMarkerSet(MarkerType markerType);

    public abstract List<String> getPlayersToRender();

}
