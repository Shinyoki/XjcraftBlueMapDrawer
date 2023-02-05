package org.xjcraft.senkosan.bluemap.manager;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Location;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;
import org.xjcraft.senkosan.bluemap.marker.HomeBaseMarkerBuilder;
import org.xjcraft.senkosan.bluemap.marker.MarkerCreator;
import org.xjcraft.senkosan.bluemap.marker.OnlinePlayerMarkerBuilder;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.xjcraft.senkosan.bluemap.constants.MapConstants.*;

/**
 * @author senko
 * @date 2022/8/13 16:43
 */
public class DefaultBlueMapManager extends ConfigurableBlueMapManager {

    private MarkerCreator markerCreator = MarkerCreator.POI;

    @Override
    public MarkerSet getMainLandOnlinePlayerMarkerSet() {
        return initMarkerSet(getMainLandMapName(), MAIN_LAND_ONLINE_PLAYER_MARKER_SET_ID);
    }

    @Override
    public MarkerSet getNetherOnlinePlayerMarkerSet() {
        return initMarkerSet(getNetherMapName(), NETHER_ONLINE_PLAYER_MARKER_SET_ID);
    }

    @Override
    public MarkerSet getTheEndOnlinePlayerMarkerSet() {
        return initMarkerSet(getTheEndMapName(), THE_END_ONLINE_PLAYER_MARKER_SET_ID);
    }

    /**
     * 渲染在线玩家
     *
     * @param playerName 玩家名称
     * @param uuid       正版UUID
     * @param dimension  所处维度 0:主世界 1:地狱 2:末地
     * @param x          x坐标
     * @param y          y坐标
     * @param z          z坐标
     */
    @Override
    public void renderOnlinePlayer(String playerName, String uuid, int dimension, double x, double y, double z) {
        renderOnlinePlayer(playerName, uuid, dimension, new Vector3d(x, y, z));
    }

    @Override
    public void renderOnlinePlayer(String playerName, String uuid, int dimension, Vector3d location) {
        Log.d("渲染在线玩家: " + playerName + " " + uuid + " " + dimension + " " + location);
        // 渲染在线玩家
        switch (dimension) {
            case 0:
                getMainLandOnlinePlayerMarkerSet().getMarkers()
                        .put(playerName, OnlinePlayerMarkerBuilder.createMarker(playerName, uuid, location));
                break;
            case 1:
                getNetherOnlinePlayerMarkerSet().getMarkers()
                        .put(playerName, OnlinePlayerMarkerBuilder.createMarker(playerName, uuid, location));
                break;
            case 2:
                getTheEndOnlinePlayerMarkerSet().getMarkers()
                        .put(playerName, OnlinePlayerMarkerBuilder.createMarker(playerName, uuid, location));
                break;
            default:
                throw new XBMPluginException("未知的维度参数");
        }
    }

    @Override
    public void clearMainLandOnlinePlayerMarker(String playerName) {
        getMainLandOnlinePlayerMarkerSet().getMarkers().remove(playerName);
    }

    @Override
    public void clearNetherOnlinePlayerMarker(String playerName) {
        getNetherOnlinePlayerMarkerSet().getMarkers().remove(playerName);
    }

    @Override
    public void clearTheEndOnlinePlayerMarker(String playerName) {
        getTheEndOnlinePlayerMarkerSet().getMarkers().remove(playerName);
    }

    @Override
    public void clearAllOnlinePlayerMarkers() {
        getMainLandOnlinePlayerMarkerSet().getMarkers().clear();
        getNetherOnlinePlayerMarkerSet().getMarkers().clear();
        getTheEndOnlinePlayerMarkerSet().getMarkers().clear();
    }

    private MarkerSet initMarkerSet(String mapName, String markerSetId) {
        Log.d("当前的地图名称: " + mapName + " 当前的标记集ID: " + markerSetId);
        Optional<BlueMapMap> map = getMapApi().getMap(mapName);
        if (map.isPresent()) {
            MarkerSet markerSet = map.get().getMarkerSets()
                    .get(markerSetId);
            if (Objects.isNull(markerSet)) {
                markerSet = buildOnlinePlayerMarkerSet();
                map.get().getMarkerSets()
                        .put(markerSetId, markerSet);
            }
            return markerSet;
        } else {
            // TODO 关闭定时任务?
            throw new XBMPluginException("地图不存在，请检查配置文件中服务器地图名称是否正确");
        }
    }

    @Override
    public void updateMainLandOnlinePlayerMarker(String playerName, String uuid, int dimension, double x, double y, double z) {
        updateMainLandOnlinePlayerMarker(playerName, uuid, dimension, new Vector3d(x, y, z));
    }

    @Override
    public void updateMainLandOnlinePlayerMarker(String playerName, String uuid, int dimension, Vector3d location) {
        Map<String, Marker> markers = null;
        switch (dimension) {
            case 0:
                // 主世界
                markers = getMainLandOnlinePlayerMarkerSet().getMarkers();
                if (markers.containsKey(playerName)) {
                    markers.get(playerName).setPosition(location);
                } else {
                    markers.put(playerName, OnlinePlayerMarkerBuilder.createMarker(playerName, uuid, location));
                }
                break;
            case 1:
                // 地狱
                markers = getNetherOnlinePlayerMarkerSet().getMarkers();
                if (markers.containsKey(playerName)) {
                    markers.get(playerName).setPosition(location);
                } else {
                    markers.put(playerName, OnlinePlayerMarkerBuilder.createMarker(playerName, uuid, location));
                }
                break;
            case 2:
                // 末地
                markers = getTheEndOnlinePlayerMarkerSet().getMarkers();
                if (markers.containsKey(playerName)) {
                    markers.get(playerName).setPosition(location);
                } else {
                    markers.put(playerName, OnlinePlayerMarkerBuilder.createMarker(playerName, uuid, location));
                }
                break;
            default:
                throw new XBMPluginException("未知的维度参数");
        }
    }

    /**
     * 创建在线玩家MarkerSet
     */
    protected static MarkerSet buildOnlinePlayerMarkerSet() {
        return MarkerSet.builder()
                .defaultHidden(false)
                .label("在线玩家")
                .toggleable(true)
                .build();
    }


    /**
     * 创建家&基地Marker
     *
     * @param playerName 玩家名称
     * @param location   基地坐标
     * @param markerType Marker类型：{@link MarkerType}
     * @param markerSet  MarkerSet
     */

    @Override
    protected void createHomeBaseMarker(String playerName, Location location, MarkerType markerType, MarkerSet markerSet) {

        switch (markerType) {
            // 只处理HOME & BASE
            case BASE:
            case HOME:
                HomeBaseMarkerBuilder.builder()
                        .playerName(playerName)
                        .markerSet(markerSet)
                        .map(getMainLandMap())
                        .location(location)
                        .markerType(markerType)
                        .build(markerCreator);
                break;
            case ONLINE_PLAYER:
                break;
        }
        ;

    }


    public void changeMarkerCreator(MarkerCreator creator) {
        this.markerCreator = creator;
    }

    public void removeAll() {
        getBaseMarkerSet()
                .getMarkers().clear();
    }

}
